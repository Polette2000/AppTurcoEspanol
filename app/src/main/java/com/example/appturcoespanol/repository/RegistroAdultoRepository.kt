package com.example.appturcoespanol.repository

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private val Context.registroAdultoDataStore by preferencesDataStore(
    name = "esturk_registro_adulto"
)

/**
 * DEMOSTRACIÓN ACADÉMICA: almacenamiento únicamente en el dispositivo.
 * No hay verificación de correo, recuperación remota, ni sincronización.
 * Conserva la lectura de cuentas anteriores con apodo y PIN de 6 dígitos.
 */
class RegistroAdultoRepository(private val context: Context) {
    companion object {
        private val CUENTAS = stringSetPreferencesKey("cuentas_registradas")
        private val CUENTA_ACTIVA = stringPreferencesKey("cuenta_activa")
        private val RECORDAR_SESION = booleanPreferencesKey("recordar_sesion_adulto")
        private val ALIAS_ANTERIOR = stringPreferencesKey("alias_adulto")
        private val SAL_ANTERIOR = stringPreferencesKey("pin_salt")
        private val VERIFICADOR_ANTERIOR = stringPreferencesKey("pin_verificador")
        private const val ITERACIONES = 180_000
        private const val ITERACIONES_ANTERIORES = 150_000
        private const val MAX_INTENTOS = 5
        private const val BLOQUEO_MS = 60_000L
        private val CORREO_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }

    val hayRegistro: Flow<Boolean> = context.registroAdultoDataStore.data.map { prefs ->
        !prefs[CUENTAS].isNullOrEmpty() || !prefs[ALIAS_ANTERIOR].isNullOrBlank()
    }

    val cuentaActiva: Flow<String?> = context.registroAdultoDataStore.data.map { prefs ->
        prefs[CUENTA_ACTIVA]
    }

    // Compatibilidad con pantallas anteriores: nombre lógico de la cuenta.
    val aliasRegistrado: Flow<String?> = context.registroAdultoDataStore.data.map { prefs ->
        prefs[CUENTA_ACTIVA] ?: prefs[ALIAS_ANTERIOR]
    }

    val cuentaRecordada: Flow<String?> = context.registroAdultoDataStore.data.map { prefs ->
        if (prefs[RECORDAR_SESION] == true) prefs[CUENTA_ACTIVA] else null
    }

    suspend fun reanudarSesionGuardada(): String? = cuentaRecordada.first()

    suspend fun cerrarSesion() {
        context.registroAdultoDataStore.edit {
            it.remove(CUENTA_ACTIVA)
            it[RECORDAR_SESION] = false
        }
    }

    /** Nuevo registro: solamente correo y contraseña. */
    suspend fun registrarConCorreo(
        correo: String,
        contrasena: String,
        mantenerSesion: Boolean
    ): Boolean {
        val email = normalizar(correo)
        if (!correoValido(email) || !contrasenaNuevaValida(contrasena)) return false

        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = generarVerificador(contrasena, sal, ITERACIONES)
        val id = idDeCuenta(email)
        var creada = false
        context.registroAdultoDataStore.edit { prefs ->
            val cuentas = prefs[CUENTAS] ?: emptySet()
            val emailDeCuentaAntigua = prefs[correoCuentaKey(email)] != null
            val esAliasAntiguo = normalizar(prefs[ALIAS_ANTERIOR].orEmpty()) == email
            if (email !in cuentas && !emailDeCuentaAntigua && !esAliasAntiguo) {
                prefs[saltKey(id)] = Base64.encodeToString(sal, Base64.NO_WRAP)
                prefs[verificadorKey(id)] = Base64.encodeToString(hash, Base64.NO_WRAP)
                prefs[tipoKey(id)] = "contrasena_2026"
                prefs[CUENTAS] = cuentas + email
                prefs[CUENTA_ACTIVA] = email
                prefs[RECORDAR_SESION] = mantenerSesion
                creada = true
            }
        }
        return creada
    }

    /**
     * Inicia sesión con correo y contraseña nuevos. Si es una cuenta anterior,
     * también admite el correo/apodo antiguo y su PIN para no perder avances.
     */
    suspend fun validarAcceso(
        identificador: String,
        clave: String,
        mantenerSesion: Boolean = false
    ): Boolean {
        val entrada = normalizar(identificador)
        if (entrada.isBlank() || clave.isEmpty()) return false
        val prefs = context.registroAdultoDataStore.data.first()
        val cuentas = prefs[CUENTAS] ?: emptySet()
        val cuenta = when {
            entrada in cuentas -> entrada
            '@' in entrada -> prefs[correoCuentaKey(entrada)] ?: return false
            else -> entrada
        }
        val esAntiguaUnica = cuenta == normalizar(prefs[ALIAS_ANTERIOR].orEmpty())
        if (cuenta !in cuentas && !esAntiguaUnica) return false
        val id = idDeCuenta(cuenta)
        if ((prefs[bloqueoKey(id)] ?: 0L) > System.currentTimeMillis()) return false

        val salTexto: String
        val hashTexto: String
        val iteraciones: Int
        if (cuenta in cuentas) {
            salTexto = prefs[saltKey(id)] ?: return false
            hashTexto = prefs[verificadorKey(id)] ?: return false
            iteraciones = if (prefs[tipoKey(id)] == "contrasena_2026") {
                ITERACIONES
            } else {
                // Los registros del Paso 19 usaban PIN y 150000 iteraciones.
                ITERACIONES_ANTERIORES
            }
            if (iteraciones == ITERACIONES_ANTERIORES && !pinAnteriorValido(clave)) return false
        } else {
            salTexto = prefs[SAL_ANTERIOR] ?: return false
            hashTexto = prefs[VERIFICADOR_ANTERIOR] ?: return false
            iteraciones = ITERACIONES_ANTERIORES
            if (!pinAnteriorValido(clave)) return false
        }

        val coincide = try {
            val sal = Base64.decode(salTexto, Base64.NO_WRAP)
            val esperado = Base64.decode(hashTexto, Base64.NO_WRAP)
            val recibido = generarVerificador(clave, sal, iteraciones)
            MessageDigest.isEqual(esperado, recibido)
        } catch (_: IllegalArgumentException) {
            false
        }

        context.registroAdultoDataStore.edit { editable ->
            if (coincide) {
                editable[CUENTA_ACTIVA] = cuenta
                editable[RECORDAR_SESION] = mantenerSesion
                editable[intentosKey(id)] = 0
                editable.remove(bloqueoKey(id))
            } else {
                val intentos = (editable[intentosKey(id)] ?: 0) + 1
                if (intentos >= MAX_INTENTOS) {
                    editable[intentosKey(id)] = 0
                    editable[bloqueoKey(id)] = System.currentTimeMillis() + BLOQUEO_MS
                } else editable[intentosKey(id)] = intentos
            }
        }
        return coincide
    }

    /** Firma antigua conservada para compatibilidad en otras pantallas. */
    suspend fun registrarAdulto(alias: String, pin: String): Boolean =
        registrarAdulto(alias, "", pin)

    /** Permite seguir creando cuentas antiguas solamente si otro módulo lo requiere. */
    suspend fun registrarAdulto(alias: String, correo: String, pin: String): Boolean {
        val nombre = normalizar(alias)
        val email = correo.takeIf { it.isNotBlank() }?.let(::normalizar)
        if (nombre.length !in 3..20 || !pinAnteriorValido(pin)) return false
        if (email != null && !correoValido(email)) return false
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = generarVerificador(pin, sal, ITERACIONES_ANTERIORES)
        val id = idDeCuenta(nombre)
        var creada = false
        context.registroAdultoDataStore.edit { prefs ->
            val cuentas = prefs[CUENTAS] ?: emptySet()
            val emailOcupado = email != null && prefs[correoCuentaKey(email)] != null
            if (nombre !in cuentas && nombre != normalizar(prefs[ALIAS_ANTERIOR].orEmpty()) && !emailOcupado) {
                prefs[saltKey(id)] = Base64.encodeToString(sal, Base64.NO_WRAP)
                prefs[verificadorKey(id)] = Base64.encodeToString(hash, Base64.NO_WRAP)
                prefs[CUENTAS] = cuentas + nombre
                if (email != null) prefs[correoCuentaKey(email)] = nombre
                prefs[CUENTA_ACTIVA] = nombre
                creada = true
            }
        }
        return creada
    }

    private fun normalizar(valor: String): String = valor.trim().lowercase(Locale.ROOT)
    private fun correoValido(valor: String) = valor.length <= 100 && CORREO_REGEX.matches(valor)
    private fun pinAnteriorValido(valor: String) = valor.length == 6 && valor.all { it in '0'..'9' }
    private fun contrasenaNuevaValida(valor: String) =
        valor.length in 8..64 && valor.any(Char::isLetter) && valor.any(Char::isDigit)

    private fun idDeCuenta(valor: String): String = MessageDigest.getInstance("SHA-256")
        .digest(valor.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 0xFF) }

    private fun correoCuentaKey(email: String) = stringPreferencesKey("correo_cuenta_${idDeCuenta(email)}")
    private fun saltKey(id: String) = stringPreferencesKey("cuenta_${id}_salt")
    private fun verificadorKey(id: String) = stringPreferencesKey("cuenta_${id}_verificador")
    private fun tipoKey(id: String) = stringPreferencesKey("cuenta_${id}_tipo")
    private fun intentosKey(id: String) = intPreferencesKey("cuenta_${id}_intentos")
    private fun bloqueoKey(id: String) = longPreferencesKey("cuenta_${id}_bloqueo")

    private fun generarVerificador(clave: String, sal: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), sal, iteraciones, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
