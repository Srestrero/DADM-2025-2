package universidadnacional.reto8.data.repository

import universidadnacional.reto8.data.database.DatabaseHelper
import universidadnacional.reto8.data.model.Clasificacion
import universidadnacional.reto8.data.model.Empresa

class EmpresaRepository(private val databaseHelper: DatabaseHelper) {

    suspend fun insertEmpresa(empresa: Empresa): Long {
        return databaseHelper.insertEmpresa(empresa)
    }

    suspend fun getAllEmpresas(): List<Empresa> {
        return databaseHelper.getAllEmpresas()
    }

    suspend fun getEmpresaById(id: Long): Empresa? {
        return databaseHelper.getEmpresaById(id)
    }

    suspend fun updateEmpresa(empresa: Empresa): Int {
        return databaseHelper.updateEmpresa(empresa)
    }

    suspend fun deleteEmpresa(id: Long): Int {
        return databaseHelper.deleteEmpresa(id)
    }

    suspend fun searchEmpresas(query: String? = null, clasificacion: Clasificacion? = null): List<Empresa> {
        return databaseHelper.searchEmpresas(query, clasificacion)
    }
}
