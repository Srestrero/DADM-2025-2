package universidadnacional.reto8.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import universidadnacional.reto8.data.database.DatabaseHelper
import universidadnacional.reto8.data.model.Empresa
import universidadnacional.reto8.data.repository.EmpresaRepository
import universidadnacional.reto8.ui.screen.DeleteConfirmationDialog
import universidadnacional.reto8.ui.screen.EmpresaFormScreen
import universidadnacional.reto8.ui.screen.EmpresaListScreen
import universidadnacional.reto8.ui.viewmodel.EmpresaViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Estado para el diálogo de eliminación
    var empresaToDelete by remember { mutableStateOf<Empresa?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Crear dependencias
    val databaseHelper = DatabaseHelper(androidx.compose.ui.platform.LocalContext.current)
    val repository = EmpresaRepository(databaseHelper)
    val viewModel = EmpresaViewModel(repository)

    if (showDeleteDialog && empresaToDelete != null) {
        DeleteConfirmationDialog(
            empresa = empresaToDelete!!,
            onConfirm = {
                viewModel.eliminarEmpresa(empresaToDelete!!.id)
                showDeleteDialog = false
                empresaToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                empresaToDelete = null
            }
        )
    }

    NavHost(navController = navController, startDestination = NavRoutes.EMPRESA_LIST) {
        composable(NavRoutes.EMPRESA_LIST) {
            EmpresaListScreen(
                viewModel = viewModel,
                onAddEmpresa = {
                    navController.navigate(NavRoutes.EMPRESA_FORM)
                },
                onEditEmpresa = { empresa ->
                    navController.navigate("${NavRoutes.EMPRESA_EDIT.replace("{empresaId}", empresa.id.toString())}")
                },
                onShowDeleteDialog = { empresa ->
                    empresaToDelete = empresa
                    showDeleteDialog = true
                }
            )
        }

        composable(NavRoutes.EMPRESA_FORM) {
            EmpresaFormScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            NavRoutes.EMPRESA_EDIT,
            arguments = listOf(navArgument("empresaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val empresaId = backStackEntry.arguments?.getLong("empresaId") ?: 0L
            val empresa = viewModel.empresas.value.find { it.id == empresaId }

            EmpresaFormScreen(
                empresa = empresa,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
