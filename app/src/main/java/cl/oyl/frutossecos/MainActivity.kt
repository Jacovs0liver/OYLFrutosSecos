package cl.oyl.frutossecos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import cl.oyl.frutossecos.ui.OylApp
import cl.oyl.frutossecos.ui.OylViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()
        val modelo = ViewModelProvider(this)[OylViewModel::class.java]
        setContent { OylApp(modelo) }
    }
}
