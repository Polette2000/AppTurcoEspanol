package com.example.appturcoespanol.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appturcoespanol.model.Avatar
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.model.Idioma
import com.example.appturcoespanol.model.ModoAcceso
import com.example.appturcoespanol.repository.ConfiguracionIdiomaRepository
import com.example.appturcoespanol.repository.ProgresoRepository
import com.example.appturcoespanol.repository.RegistroAdultoRepository
import com.example.appturcoespanol.ui.screens.AccesoInicialScreen
import com.example.appturcoespanol.ui.screens.BienvenidaScreen
import com.example.appturcoespanol.ui.screens.CamaraScreen
import com.example.appturcoespanol.ui.screens.CategoriasScreen
import com.example.appturcoespanol.ui.screens.ConfiguracionIdiomaScreen
import com.example.appturcoespanol.ui.screens.ConfiguracionSesionScreen
import com.example.appturcoespanol.ui.screens.JuegoScreen
import com.example.appturcoespanol.ui.screens.ModoInvitadoScreen
import com.example.appturcoespanol.ui.screens.ProgresoScreen
import com.example.appturcoespanol.ui.screens.RegistroAdultoScreen
import com.example.appturcoespanol.ui.screens.VocabularioScreen
import com.example.appturcoespanol.viewmodel.CategoriasViewModel
import com.example.appturcoespanol.viewmodel.ConfiguracionIdiomaViewModel
import com.example.appturcoespanol.viewmodel.ConfiguracionSesionViewModel
import com.example.appturcoespanol.viewmodel.JuegoViewModel
import com.example.appturcoespanol.viewmodel.ProgresoViewModel
import com.example.appturcoespanol.viewmodel.RegistroAdultoViewModel
import com.example.appturcoespanol.viewmodel.VocabularioViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Locale

/**
 * Paso 23: flujo bilingüe con acceso por correo y contraseña.
 * Bienvenida (elegir idioma) -> Crear cuenta / Iniciar sesion ->
 * Avatares -> Categorias -> Aprendizaje. La ruta de idioma antiguo se conserva.
 * La cuenta del adulto es local y se valida antes de dar acceso al aprendizaje.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()

    val registroRepository = remember(context) { RegistroAdultoRepository(context) }
    val idiomasRepository = remember(context) { ConfiguracionIdiomaRepository(context) }

    // Solo los datos de sesión; no se almacenan las contraseñas ni el correo en este estado.
    var cuentaActiva by rememberSaveable { mutableStateOf("") }
    var avatarActivo by rememberSaveable { mutableStateOf<Avatar?>(null) }
    // Se elige en la primera pantalla y traduce el acceso antes de iniciar sesión.
    var idiomaInicial by rememberSaveable { mutableStateOf<Idioma?>(null) }

    // Se aplica el idioma guardado por cuenta, no el del último avatar utilizado.
    val idiomaNativo by remember(cuentaActiva, idiomasRepository) {
        if (cuentaActiva.isBlank()) flowOf(null)
        else idiomasRepository.observarIdiomaNativo(cuentaActiva)
    }.collectAsStateWithLifecycle(initialValue = null)

    // Progreso por cuenta y avatar, sin mezclar familias en el mismo dispositivo.
    val identificadorCuenta = remember(cuentaActiva) {
        cuentaActiva.takeIf { it.isNotBlank() }?.let { cuenta ->
            val bytes = MessageDigest.getInstance("SHA-256")
                .digest(cuenta.trim().lowercase(Locale.ROOT).toByteArray(Charsets.UTF_8))
            "cuenta_" + bytes.joinToString("") { "%02x".format(it.toInt() and 0xFF) }
        }
    }
    val progresoRepository = remember(context, identificadorCuenta) {
        ProgresoRepository(context, perfilId = identificadorCuenta)
    }

    val registroFactory = remember(registroRepository) {
        viewModelFactory { initializer { RegistroAdultoViewModel(registroRepository) } }
    }
    val idiomaFactory = remember(idiomasRepository, cuentaActiva) {
        viewModelFactory {
            initializer { ConfiguracionIdiomaViewModel(idiomasRepository, cuentaActiva) }
        }
    }
    val vocabularioFactory = remember(progresoRepository) {
        viewModelFactory { initializer { VocabularioViewModel(progresoRepository) } }
    }
    val juegoFactory = remember(progresoRepository) {
        viewModelFactory { initializer { JuegoViewModel(progresoRepository) } }
    }
    val progresoFactory = remember(progresoRepository) {
        viewModelFactory { initializer { ProgresoViewModel(progresoRepository) } }
    }

    // Volver al acceso requiere autenticar de nuevo, pero NO borra cuentas
    // ni elimina el progreso almacenado en DataStore.
    fun cerrarAccesoYVolver() {
        scope.launch {
            registroRepository.cerrarSesion()
            cuentaActiva = ""
            avatarActivo = null
            navController.navigate("acceso-inicial") {
                popUpTo("bienvenida") { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "bienvenida") {
        composable("bienvenida") {
            BienvenidaScreen(
                idiomaInterfaz = idiomaInicial,
                onSeleccionarIdioma = { idiomaInicial = it },
                onComenzar = {
                    // Siempre Bienvenida -> Crear cuenta / Iniciar sesión.
                    if (idiomaInicial != null) {
                        navController.navigate("acceso-inicial") { launchSingleTop = true }
                    }
                }
            )
        }

        composable("acceso-inicial") {
            AccesoInicialScreen(
                onCrearCuenta = { navController.navigate("registro-adulto/crear") },
                onIniciarSesion = { navController.navigate("registro-adulto/ingresar") },
                onVolver = { navController.popBackStack() },
                idiomaInterfaz = idiomaInicial ?: Idioma.ESPANOL
            )
        }

        // Modo de demostracion conservado, pero no hay entrada desde el
        // flujo principal: el inicio siempre pasa por la cuenta adulta.
        composable("invitado") {
            ModoInvitadoScreen(
                onVolver = { navController.popBackStack() },
                onConfigurar = { navController.navigate("registro-adulto/crear") }
            )
        }

        composable("registro-adulto/{modo}") { entrada ->
            val modoDeseado = if (entrada.arguments?.getString("modo") == "crear") {
                ModoAcceso.REGISTRARSE
            } else {
                ModoAcceso.INICIAR_SESION
            }
            val registroViewModel: RegistroAdultoViewModel = viewModel(factory = registroFactory)
            LaunchedEffect(modoDeseado) { registroViewModel.seleccionarModo(modoDeseado) }
            RegistroAdultoScreen(
                viewModel = registroViewModel,
                onVolver = { navController.popBackStack() },
                idiomaInterfaz = idiomaInicial ?: Idioma.ESPANOL,
                onSeleccionarIdioma = { idiomaInicial = it },
                onAccesoAutorizado = {
                    // El ViewModel entrega el identificador interno de la cuenta validada.
                    val cuenta = registroViewModel.uiState.value.cuentaAutorizada.trim()
                    if (cuenta.isNotBlank()) {
                        cuentaActiva = cuenta
                        avatarActivo = null
                        scope.launch {
                            // La elección de Bienvenida se guarda para esta cuenta.
                            // Si el adulto elige otro idioma en otro inicio, se actualiza.
                            val resultado = runCatching {
                                val elegido = idiomaInicial
                                if (elegido != null) {
                                    idiomasRepository.guardarIdiomaNativo(cuenta, elegido)
                                }
                                idiomasRepository.observarIdiomaNativo(cuenta).first()
                            }
                            navController.navigate(
                                if (resultado.getOrNull() == null) "idiomas" else "avatares"
                            ) {
                                popUpTo("acceso-inicial") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable("idiomas") {
            if (cuentaActiva.isBlank()) {
                Text("Un adulto debe configurar la cuenta primero.")
            } else {
                val idiomasViewModel: ConfiguracionIdiomaViewModel = viewModel(
                    key = "idioma_$cuentaActiva", factory = idiomaFactory
                )
                ConfiguracionIdiomaScreen(
                    viewModel = idiomasViewModel,
                    onVolver = { cerrarAccesoYVolver() },
                    onContinuar = {
                        navController.navigate("avatares") {
                            popUpTo("idiomas") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        // ¡Aquí el niño elige libremente Zorro, Panda o Gato!
        composable("avatares") {
            if (cuentaActiva.isBlank()) {
                Text("Inicia sesión para comenzar a jugar.")
            } else {
                val avatarViewModel: ConfiguracionSesionViewModel = viewModel(
                    key = "avatar_$cuentaActiva"
                )
                ConfiguracionSesionScreen(
                    viewModel = avatarViewModel,
                    // Cambiar de cuenta desde aqui vuelve al acceso del adulto.
                    onVolver = { cerrarAccesoYVolver() },
                    onVerCategorias = {
                        val seleccionado = avatarViewModel.uiState.value.avatarSeleccionado
                        if (seleccionado != null) {
                            avatarActivo = seleccionado
                            navController.navigate("categorias") { launchSingleTop = true }
                        }
                    }
                )
            }
        }

        composable("categorias") {
            val avatar = avatarActivo
            val idioma = idiomaNativo
            if (avatar != null && idioma != null) {
                val categoriasViewModel: CategoriasViewModel = viewModel(
                    key = "categorias_${cuentaActiva}_${avatar.name}"
                )
                CategoriasScreen(
                    viewModel = categoriasViewModel,
                    idiomaNativo = idioma,
                    avatar = avatar,
                    onVolver = {
                        // Permite cambiar de avatar SIN pedir registro o PIN.
                        navController.popBackStack("avatares", inclusive = false)
                    },
                    onAbrirLeccion = { categoria ->
                        if (categoria.avatar == avatar) {
                            navController.navigate("vocabulario/${categoria.name}")
                        }
                    },
                    onVerProgreso = { navController.navigate("progreso") }
                )
            } else {
                Text("Elige un idioma y un avatar para continuar.")
            }
        }

        composable("vocabulario/{categoria}") { entrada ->
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == entrada.arguments?.getString("categoria")
            }
            val avatar = avatarActivo
            val idioma = idiomaNativo
            if (categoria != null && avatar != null && idioma != null && categoria.avatar == avatar) {
                val vocabViewModel: VocabularioViewModel = viewModel(
                    key = "vocab_${cuentaActiva}_${avatar.name}_${categoria.name}",
                    factory = vocabularioFactory
                )
                VocabularioScreen(
                    categoria = categoria,
                    avatar = avatar,
                    idiomaNativo = idioma,
                    viewModel = vocabViewModel,
                    onVolver = { navController.popBackStack() },
                    onAbrirJuego = { navController.navigate("juego/${categoria.name}") },
                    onAbrirCamara = { navController.navigate("camara/${categoria.name}") }
                )
            } else Text("Selecciona un mundo y una categoría válida.")
        }

        composable("juego/{categoria}") { entrada ->
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == entrada.arguments?.getString("categoria")
            }
            val avatar = avatarActivo
            val idioma = idiomaNativo
            if (categoria != null && avatar != null && idioma != null && categoria.avatar == avatar) {
                val juegoViewModel: JuegoViewModel = viewModel(
                    key = "juego_${cuentaActiva}_${avatar.name}_${categoria.name}",
                    factory = juegoFactory
                )
                JuegoScreen(
                    categoria = categoria,
                    avatar = avatar,
                    idiomaNativo = idioma,
                    viewModel = juegoViewModel,
                    onVolver = { navController.popBackStack() }
                )
            } else Text("Este juego no está disponible para el avatar seleccionado.")
        }

        composable("progreso") {
            val avatar = avatarActivo
            val idioma = idiomaNativo
            if (avatar != null && idioma != null) {
                val progresoViewModel: ProgresoViewModel = viewModel(
                    key = "progreso_${cuentaActiva}_${avatar.name}",
                    factory = progresoFactory
                )
                ProgresoScreen(
                    avatar = avatar,
                    idiomaNativo = idioma,
                    viewModel = progresoViewModel,
                    onVolver = { navController.popBackStack() },
                    permitirReinicio = false  // Un niño no puede borrar sus avances.
                )
            } else Text("Selecciona un avatar para ver su progreso.")
        }

        composable("camara/{categoria}") { entrada ->
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == entrada.arguments?.getString("categoria")
            }
            val avatar = avatarActivo
            if (categoria != null && avatar != null && categoria.avatar == avatar) {
                CamaraScreen(
                    categoria = categoria,
                    onVolver = { navController.popBackStack() }
                )
            } else Text("Categoría no disponible para este avatar.")
        }
    }
}
