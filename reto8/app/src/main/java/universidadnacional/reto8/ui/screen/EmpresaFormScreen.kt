package universidadnacional.reto8.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import universidadnacional.reto8.data.model.Clasificacion
import universidadnacional.reto8.data.model.Empresa
import universidadnacional.reto8.ui.viewmodel.EmpresaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaFormScreen(
    empresa: Empresa? = null,
    viewModel: EmpresaViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var nombre by remember { mutableStateOf(empresa?.nombre ?: "") }
    var url by remember { mutableStateOf(empresa?.url ?: "") }
    var telefono by remember { mutableStateOf(empresa?.telefono ?: "") }
    var email by remember { mutableStateOf(empresa?.email ?: "") }
    var productosServicios by remember { mutableStateOf(empresa?.productosServicios ?: "") }
    var clasificacion by remember { mutableStateOf(empresa?.clasificacion ?: Clasificacion.CONSULTORIA) }
    var expanded by remember { mutableStateOf(false) }

    val isEditMode = empresa != null
    val isFormValid = nombre.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Empresa" else "Nueva Empresa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de error
            error?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la empresa *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombre.isBlank(),
                supportingText = {
                    if (nombre.isBlank()) {
                        Text("Este campo es obligatorio")
                    }
                }
            )

            // Campo Clasificación
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = clasificacion.toDisplayString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Clasificación *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Clasificacion.entries.forEach { clasif ->
                        DropdownMenuItem(
                            text = { Text(clasif.toDisplayString()) },
                            onClick = {
                                clasificacion = clasif
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Campo URL
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL de la página web") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://www.ejemplo.com") }
            )

            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de contacto") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("+57 300 123 4567") }
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email de contacto") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("contacto@empresa.com") }
            )

            // Campo Productos y Servicios
            OutlinedTextField(
                value = productosServicios,
                onValueChange = { productosServicios = it },
                label = { Text("Productos y servicios") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Describa los productos y servicios que ofrece la empresa...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de guardar
            Button(
                onClick = {
                    if (isFormValid) {
                        val empresaData = Empresa(
                            id = empresa?.id ?: 0,
                            nombre = nombre.trim(),
                            url = url.trim(),
                            telefono = telefono.trim(),
                            email = email.trim(),
                            productosServicios = productosServicios.trim(),
                            clasificacion = clasificacion
                        )

                        if (isEditMode) {
                            viewModel.actualizarEmpresa(empresaData)
                        } else {
                            viewModel.insertarEmpresa(empresaData)
                        }

                        // Limpiar error y navegar de vuelta
                        viewModel.limpiarError()
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isEditMode) "Actualizar Empresa" else "Guardar Empresa")
                }
            }
        }
    }
}
