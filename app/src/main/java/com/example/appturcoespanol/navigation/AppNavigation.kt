
package com.example.appturcoespanol.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appturcoespanol.model.CategoriaAprendizaje
import com.example.appturcoespanol.ui.screens.BienvenidaScreen
import com.example.appturcoespanol.ui.screens.CategoriasScreen
import com.example.appturcoespanol.ui.screens.ConfiguracionSesionScreen
import com.example.appturcoespanol.ui.screens.VocabularioScreen
import com.example.appturcoespanol.viewmodel.CategoriasViewModel
import com.example.appturcoespanol.viewmodel.ConfiguracionSesionViewModel
import com.example.appturcoespanol.viewmodel.VocabularioViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "bienvenida"
    ) {

        // Pantalla 1: Bienvenida
        composable("bienvenida") {

            BienvenidaScreen(
                onComenzar = {
                    navController.navigate("configuracion")
                }
            )
        }

        // Pantalla 2: Configuracion
        composable("configuracion") {

            val configuracionViewModel:
                    ConfiguracionSesionViewModel = viewModel()

            ConfiguracionSesionScreen(
                viewModel = configuracionViewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onVerCategorias = {
                    navController.navigate("categorias")
                }
            )
        }

        // Pantalla 3: Categorias
        composable("categorias") {

            val categoriasViewModel:
                    CategoriasViewModel = viewModel()

            CategoriasScreen(
                viewModel = categoriasViewModel,
                onVolver = {
                    navController.popBackStack()
                },
                onAbrirLeccion = { categoria ->

                    navController.navigate(
                        "vocabulario/${categoria.name}"
                    )
                }
            )
        }

        // Pantalla 4: Vocabulario
        composable("vocabulario/{categoria}") { backStackEntry ->

            val nombreCategoria =
                backStackEntry.arguments?.getString("categoria")

            val categoria =
                CategoriaAprendizaje.entries.firstOrNull {
                    it.name == nombreCategoria
                }

            if (categoria != null) {

                val vocabularioViewModel:
                        VocabularioViewModel = viewModel()

                VocabularioScreen(
                    categoria = categoria,
                    viewModel = vocabularioViewModel,
                    onVolver = {
                        navController.popBackStack()
                    }
                )

            } else {
                Text("Categoría no disponible")
            }
        }
    }
}
