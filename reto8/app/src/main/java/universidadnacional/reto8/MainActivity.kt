package universidadnacional.reto8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import universidadnacional.reto8.ui.navigation.AppNavigation
import universidadnacional.reto8.ui.theme.Reto8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto8Theme {
                AppNavigation()
            }
        }
    }
}