package com.example.appturcoespanol.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.repository.ProgresoRepository
import com.example.appturcoespanol.ui.screens.BienvenidaScreen
import com.example.appturcoespanol.ui.screens.CamaraScreen
import com.example.appturcoespanol.ui.screens.CategoriasScreen
import com.example.appturcoespanol.ui.screens.ConfiguracionSesionScreen
import com.example.appturcoespanol.ui.screens.JuegoScreen
import com.example.appturcoespanol.ui.screens.ProgresoScreen
import com.example.appturcoespanol.ui.screens.VocabularioScreen
import com.example.appturcoespanol.viewmodel.CategoriasViewModel
import com.example.appturcoespanol.viewmodel.ConfiguracionSesionViewModel
import com.example.appturcoespanol.viewmodel.JuegoViewModel
import com.example.appturcoespanol.viewmodel.ProgresoViewModel
import com.example.appturcoespanol.viewmodel.VocabularioViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext

    // Un repositorio DataStore compartido por los ViewModels.
    val progresoRepository = remember(context) {
        ProgresoRepository(context)
    }

    val vocabularioFactory = remember(progresoRepository) {
        viewModelFactory {
            initializer { VocabularioViewModel(progresoRepository) }
        }
    }

    val juegoFactory = remember(progresoRepository) {
        viewModelFactory {
            initializer { JuegoViewModel(progresoRepository) }
        }
    }

    val progresoFactory = remember(progresoRepository) {
        viewModelFactory {
            initializer { ProgresoViewModel(progresoRepository) }
        }
    }

    // Se comparte la seleccion del avatar entre pantallas.
    val configuracionViewModel: ConfiguracionSesionViewModel = viewModel()
    val estadoSesion by configuracionViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {
        // 1. Pantalla de inicio
        composable("bienvenida") {
            BienvenidaScreen(
                onComenzar = { navController.navigate("configuracion") }
            )
        }

        // 2. Elegir avatar
        composable("configuracion") {
            ConfiguracionSesionScreen(
                viewModel = configuracionViewModel,
                onVolver = { navController.popBackStack() },
                onVerCategorias = { navController.navigate("categorias") }
            )
        }

        // 3. Elegir categoria y consultar progreso
        composable("categorias") {
            val categoriasViewModel: CategoriasViewModel = viewModel()

            CategoriasScreen(
                viewModel = categoriasViewModel,
                onVolver = { navController.popBackStack() },
                onAbrirLeccion = { categoria ->
                    navController.navigate("vocabulario/${categoria.name}")
                },
                onVerProgreso = { navController.navigate("progreso") }
            )
        }

        // 4. Vocabulario, con acceso al juego y a la camara
        composable("vocabulario/{categoria}") { backStackEntry ->
            val nombreCategoria = backStackEntry.arguments?.getString("categoria")
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == nombreCategoria
            }
            val avatar = estadoSesion.avatarSeleccionado

            if (categoria != null && avatar != null) {
                val vocabularioViewModel: VocabularioViewModel = viewModel(
                    factory = vocabularioFactory
                )

                VocabularioScreen(
                    categoria = categoria,
                    avatar = avatar,
                    viewModel = vocabularioViewModel,
                    onVolver = { navController.popBackStack() },
                    onAbrirJuego = {
                        navController.navigate("juego/${categoria.name}")
                    },
                    onAbrirCamara = {
                        navController.navigate("camara/${categoria.name}")
                    }
                )
            } else {
                Text("Selecciona un avatar y una categoría para continuar.")
            }
        }

        // 5. Minijuego con guardado de la puntuacion
        composable("juego/{categoria}") { backStackEntry ->
            val nombreCategoria = backStackEntry.arguments?.getString("categoria")
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == nombreCategoria
            }
            val avatar = estadoSesion.avatarSeleccionado

            if (categoria != null && avatar != null) {
                val juegoViewModel: JuegoViewModel = viewModel(factory = juegoFactory)

                JuegoScreen(
                    categoria = categoria,
                    avatar = avatar,
                    viewModel = juegoViewModel,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text("Selecciona un avatar y una categoría para continuar.")
            }
        }

        // 6. Progreso y reinicio seguro
        composable("progreso") {
            val avatar = estadoSesion.avatarSeleccionado

            if (avatar != null) {
                val progresoViewModel: ProgresoViewModel = viewModel(
                    factory = progresoFactory
                )

                ProgresoScreen(
                    avatar = avatar,
                    viewModel = progresoViewModel,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text("Selecciona un avatar para ver tu progreso.")
            }
        }

        // 7. NUEVO: Camara del sistema para fotografiar objetos
        composable("camara/{categoria}") { backStackEntry ->
            val nombreCategoria = backStackEntry.arguments?.getString("categoria")
            val categoria = CategoriaAprendizaje.entries.firstOrNull {
                it.name == nombreCategoria
            }

            if (categoria != null) {
                CamaraScreen(
                    categoria = categoria,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text("Categoría no disponible.")
            }
        }
    }
}
