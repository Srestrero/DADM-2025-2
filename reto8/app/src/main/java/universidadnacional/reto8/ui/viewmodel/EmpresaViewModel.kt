package universidadnacional.reto8.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import universidadnacional.reto8.data.model.Clasificacion
import universidadnacional.reto8.data.model.Empresa
import universidadnacional.reto8.data.repository.EmpresaRepository

class EmpresaViewModel(private val repository: EmpresaRepository) : ViewModel() {

    private val _empresas = MutableStateFlow<List<Empresa>>(emptyList())
    val empresas: StateFlow<List<Empresa>> = _empresas.asStateFlow()

    private val _empresaSeleccionada = MutableStateFlow<Empresa?>(null)
    val empresaSeleccionada: StateFlow<Empresa?> = _empresaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarEmpresas()
    }

    fun cargarEmpresas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val listaEmpresas = repository.getAllEmpresas()
                _empresas.value = listaEmpresas
            } catch (e: Exception) {
                _error.value = "Error al cargar empresas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun buscarEmpresas(query: String? = null, clasificacion: Clasificacion? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val listaEmpresas = repository.searchEmpresas(query, clasificacion)
                _empresas.value = listaEmpresas
            } catch (e: Exception) {
                _error.value = "Error al buscar empresas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun insertarEmpresa(empresa: Empresa) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.insertEmpresa(empresa)
                cargarEmpresas() // Recargar la lista después de insertar
            } catch (e: Exception) {
                _error.value = "Error al insertar empresa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarEmpresa(empresa: Empresa) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.updateEmpresa(empresa)
                cargarEmpresas() // Recargar la lista después de actualizar
            } catch (e: Exception) {
                _error.value = "Error al actualizar empresa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarEmpresa(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.deleteEmpresa(id)
                cargarEmpresas() // Recargar la lista después de eliminar
            } catch (e: Exception) {
                _error.value = "Error al eliminar empresa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun seleccionarEmpresa(empresa: Empresa?) {
        _empresaSeleccionada.value = empresa
    }

    fun limpiarError() {
        _error.value = null
    }
}
