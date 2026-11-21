package com.example.appturcoespanol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appturcoespanol.model.ModoAcceso
import com.example.appturcoespanol.model.RegistroAdultoUiState
import com.example.appturcoespanol.repository.RegistroAdultoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class RegistroAdultoViewModel(
    private val repository: RegistroAdultoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroAdultoUiState())
    val uiState: StateFlow<RegistroAdultoUiState> = _uiState.asStateFlow()

    init { comprobarRegistro() }

    private fun comprobarRegistro() {
        viewModelScope.launch {
            try {
                val hayCuentas = repository.hayRegistro.first()
                val cuentaRecordada = repository.cuentaRecordada.first()
                _uiState.update {
                    it.copy(hayRegistro = hayCuentas, sesionGuardada = !cuentaRecordada.isNullOrBlank())
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(hayRegistro = false, mensajeGeneral = "No se pudo leer el acceso local.") }
            }
        }
    }

    fun seleccionarModo(modo: ModoAcceso) {
        if (_uiState.value.procesando) return
        _uiState.update {
            it.copy(
                modo = modo,
                correo = "", contrasena = "", confirmarContrasena = "",
                errorCorreo = null, errorContrasena = null, errorConfirmacion = null,
                mensajeGeneral = null, registroCompletado = false, cuentaAutorizada = ""
            )
        }
    }

    fun actualizarCorreo(valor: String) = _uiState.update {
        it.copy(correo = valor.take(100), errorCorreo = null, mensajeGeneral = null)
    }

    fun actualizarContrasena(valor: String) = _uiState.update {
        it.copy(contrasena = valor.take(64), errorContrasena = null, mensajeGeneral = null)
    }

    fun actualizarConfirmacion(valor: String) = _uiState.update {
        it.copy(confirmarContrasena = valor.take(64), errorConfirmacion = null, mensajeGeneral = null)
    }

    fun actualizarRecordarSesion(valor: Boolean) = _uiState.update {
        it.copy(recordarSesion = valor)
    }

    fun enviarFormulario() {
        val actual = _uiState.value
        if (actual.procesando || actual.registroCompletado) return
        val registro = actual.modo == ModoAcceso.REGISTRARSE
        val correo = actual.correo.trim().lowercase(Locale.ROOT)
        val regexCorreo = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        val errorCorreo = when {
            correo.isBlank() -> "Ingresa tu correo electrónico."
            registro && !regexCorreo.matches(correo) -> "Ingresa un correo válido."
            !registro && correo.contains('@') && !regexCorreo.matches(correo) -> "Ingresa un correo válido."
            else -> null
        }
        val errorClave = when {
            actual.contrasena.isBlank() -> "Ingresa una contraseña."
            registro && (actual.contrasena.length !in 8..64 ||
                    !actual.contrasena.any(Char::isLetter) ||
                    !actual.contrasena.any(Char::isDigit)) -> "Usa 8 a 64 caracteres, incluyendo letras y números."
            else -> null
        }
        val errorConfirmar = if (registro && actual.contrasena != actual.confirmarContrasena) {
            "Las contraseñas no coinciden."
        } else null
        _uiState.update {
            it.copy(
                errorCorreo = errorCorreo,
                errorContrasena = errorClave,
                errorConfirmacion = errorConfirmar,
                mensajeGeneral = null
            )
        }
        if (errorCorreo != null || errorClave != null || errorConfirmar != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true) }
            try {
                val exito = if (registro) {
                    repository.registrarConCorreo(correo, actual.contrasena, actual.recordarSesion)
                } else {
                    repository.validarAcceso(correo, actual.contrasena, actual.recordarSesion)
                }
                val cuenta = if (exito) repository.cuentaActiva.first().orEmpty() else ""
                _uiState.update {
                    if (exito && cuenta.isNotBlank()) {
                        it.copy(
                            procesando = false, registroCompletado = true,
                            hayRegistro = true, sesionGuardada = actual.recordarSesion,
                            cuentaAutorizada = cuenta, contrasena = "", confirmarContrasena = ""
                        )
                    } else {
                        it.copy(
                            procesando = false,
                            contrasena = "", confirmarContrasena = "",
                            mensajeGeneral = if (registro) {
                                "Este correo ya está registrado en el dispositivo."
                            } else {
                                "Correo o contraseña incorrectos, o acceso temporalmente bloqueado."
                            }
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        procesando = false, contrasena = "", confirmarContrasena = "",
                        mensajeGeneral = "No se pudo completar el acceso."
                    )
                }
            }
        }
    }

    /** Permite continuar con una sesión local recordada sin volver a escribir la clave. */
    fun continuarSesionGuardada() {
        if (_uiState.value.procesando || _uiState.value.registroCompletado) return
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true) }
            try {
                val cuenta = repository.reanudarSesionGuardada().orEmpty()
                _uiState.update {
                    if (cuenta.isNotBlank()) it.copy(
                        procesando = false, registroCompletado = true,
                        cuentaAutorizada = cuenta
                    ) else it.copy(
                        procesando = false, sesionGuardada = false,
                        mensajeGeneral = "No hay una sesión local disponible."
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(procesando = false, mensajeGeneral = "No se pudo continuar la sesión.")
                }
            }
        }
    }
}
