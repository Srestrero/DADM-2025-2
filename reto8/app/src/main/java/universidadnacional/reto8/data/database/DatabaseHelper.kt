package universidadnacional.reto8.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import universidadnacional.reto8.data.model.Clasificacion
import universidadnacional.reto8.data.model.Empresa

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "empresas.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_EMPRESAS = "empresas"

        // Columnas de la tabla
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_URL = "url"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PRODUCTOS_SERVICIOS = "productos_servicios"
        const val COLUMN_CLASIFICACION = "clasificacion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_EMPRESAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_URL TEXT,
                $COLUMN_TELEFONO TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PRODUCTOS_SERVICIOS TEXT,
                $COLUMN_CLASIFICACION TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Insertar datos de ejemplo
        insertSampleData(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val sampleData = listOf(
            ContentValues().apply {
                put(COLUMN_NOMBRE, "TechSolutions S.A.")
                put(COLUMN_URL, "https://www.techsolutions.com")
                put(COLUMN_TELEFONO, "+57 300 123 4567")
                put(COLUMN_EMAIL, "contacto@techsolutions.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Desarrollo de software personalizado, consultoría IT, mantenimiento de sistemas")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "InnovateSoft")
                put(COLUMN_URL, "https://www.innovatesoft.co")
                put(COLUMN_TELEFONO, "+57 301 234 5678")
                put(COLUMN_EMAIL, "info@innovatesoft.co")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Desarrollo de aplicaciones móviles, soluciones web, análisis de datos")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "Global Software Factory")
                put(COLUMN_URL, "https://www.globalsoftwarefactory.com")
                put(COLUMN_TELEFONO, "+57 302 345 6789")
                put(COLUMN_EMAIL, "business@globalsoftwarefactory.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Fábrica de software, desarrollo ágil, outsourcing de TI")
                put(COLUMN_CLASIFICACION, Clasificacion.FABRICA_SOFTWARE.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "ConsultIT Pro")
                put(COLUMN_URL, "https://www.consultitpro.com")
                put(COLUMN_TELEFONO, "+57 303 456 7890")
                put(COLUMN_EMAIL, "consultas@consultitpro.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Consultoría estratégica, auditoría de sistemas, transformación digital")
                put(COLUMN_CLASIFICACION, Clasificacion.CONSULTORIA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "DevMasters")
                put(COLUMN_URL, "https://www.devmasters.dev")
                put(COLUMN_TELEFONO, "+57 304 567 8901")
                put(COLUMN_EMAIL, "team@devmasters.dev")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Desarrollo web full-stack, aplicaciones móviles nativas, DevOps")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "CodeCraft Solutions")
                put(COLUMN_URL, "https://www.codecraft.com")
                put(COLUMN_TELEFONO, "+57 305 678 9012")
                put(COLUMN_EMAIL, "contact@codecraft.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Desarrollo de software a medida, integración de sistemas, soporte técnico")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "AgileTech")
                put(COLUMN_URL, "https://www.agiletech.co")
                put(COLUMN_TELEFONO, "+57 306 789 0123")
                put(COLUMN_EMAIL, "hello@agiletech.co")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Metodologías ágiles, transformación digital, formación en desarrollo")
                put(COLUMN_CLASIFICACION, Clasificacion.CONSULTORIA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "MobileFirst Apps")
                put(COLUMN_URL, "https://www.mobilefirstapps.com")
                put(COLUMN_TELEFONO, "+57 307 890 1234")
                put(COLUMN_EMAIL, "dev@mobilefirstapps.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Aplicaciones móviles iOS y Android, diseño UX/UI, testing móvil")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "CloudSys Pro")
                put(COLUMN_URL, "https://www.cloudsyspro.com")
                put(COLUMN_TELEFONO, "+57 308 901 2345")
                put(COLUMN_EMAIL, "support@cloudsyspro.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Soluciones en la nube, migración a cloud, infraestructura as a service")
                put(COLUMN_CLASIFICACION, Clasificacion.CONSULTORIA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "DataFlow Analytics")
                put(COLUMN_URL, "https://www.dataflowanalytics.com")
                put(COLUMN_TELEFONO, "+57 309 012 3456")
                put(COLUMN_EMAIL, "analytics@dataflowanalytics.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Business Intelligence, análisis de datos, machine learning, dashboards interactivos")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "SecureCode Ltda")
                put(COLUMN_URL, "https://www.securecode.com.co")
                put(COLUMN_TELEFONO, "+57 310 123 4567")
                put(COLUMN_EMAIL, "security@securecode.com.co")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Auditoría de seguridad, desarrollo seguro, pruebas de penetración")
                put(COLUMN_CLASIFICACION, Clasificacion.CONSULTORIA.name)
            },
            ContentValues().apply {
                put(COLUMN_NOMBRE, "WebSolutions Pro")
                put(COLUMN_URL, "https://www.websolutionspro.com")
                put(COLUMN_TELEFONO, "+57 311 234 5678")
                put(COLUMN_EMAIL, "web@websolutionspro.com")
                put(COLUMN_PRODUCTOS_SERVICIOS, "Desarrollo web responsive, e-commerce, CMS personalizado, SEO")
                put(COLUMN_CLASIFICACION, Clasificacion.DESARROLLO_MEDIDA.name)
            }
        )

        sampleData.forEach { values ->
            db.insert(TABLE_EMPRESAS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMPRESAS")
        onCreate(db)
    }

    // Operaciones CRUD

    fun insertEmpresa(empresa: Empresa): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, empresa.nombre)
            put(COLUMN_URL, empresa.url)
            put(COLUMN_TELEFONO, empresa.telefono)
            put(COLUMN_EMAIL, empresa.email)
            put(COLUMN_PRODUCTOS_SERVICIOS, empresa.productosServicios)
            put(COLUMN_CLASIFICACION, empresa.clasificacion.name)
        }
        val id = db.insert(TABLE_EMPRESAS, null, values)
        db.close()
        return id
    }

    fun getAllEmpresas(): List<Empresa> {
        val empresas = mutableListOf<Empresa>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_EMPRESAS,
            null, null, null, null, null, "$COLUMN_NOMBRE ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val empresa = cursorToEmpresa(cursor)
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return empresas
    }

    fun getEmpresaById(id: Long): Empresa? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_EMPRESAS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var empresa: Empresa? = null
        if (cursor.moveToFirst()) {
            empresa = cursorToEmpresa(cursor)
        }

        cursor.close()
        db.close()
        return empresa
    }

    fun updateEmpresa(empresa: Empresa): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, empresa.nombre)
            put(COLUMN_URL, empresa.url)
            put(COLUMN_TELEFONO, empresa.telefono)
            put(COLUMN_EMAIL, empresa.email)
            put(COLUMN_PRODUCTOS_SERVICIOS, empresa.productosServicios)
            put(COLUMN_CLASIFICACION, empresa.clasificacion.name)
        }
        val rowsAffected = db.update(
            TABLE_EMPRESAS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(empresa.id.toString())
        )
        db.close()
        return rowsAffected
    }

    fun deleteEmpresa(id: Long): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(
            TABLE_EMPRESAS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected
    }

    fun searchEmpresas(query: String? = null, clasificacion: Clasificacion? = null): List<Empresa> {
        val empresas = mutableListOf<Empresa>()
        val db = readableDatabase

        val selection = StringBuilder()
        val selectionArgs = mutableListOf<String>()

        if (!query.isNullOrEmpty()) {
            selection.append("$COLUMN_NOMBRE LIKE ?")
            selectionArgs.add("%$query%")
        }

        if (clasificacion != null) {
            if (selection.isNotEmpty()) {
                selection.append(" AND ")
            }
            selection.append("$COLUMN_CLASIFICACION = ?")
            selectionArgs.add(clasificacion.name)
        }

        val cursor: Cursor = db.query(
            TABLE_EMPRESAS,
            null,
            if (selection.isEmpty()) null else selection.toString(),
            if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray(),
            null, null, "$COLUMN_NOMBRE ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val empresa = cursorToEmpresa(cursor)
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return empresas
    }

    private fun cursorToEmpresa(cursor: Cursor): Empresa {
        return Empresa(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
            url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
            telefono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
            productosServicios = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTOS_SERVICIOS)),
            clasificacion = Clasificacion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASIFICACION)))
        )
    }
}
