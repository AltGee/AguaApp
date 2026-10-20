package corona.jesus.aguaapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import corona.jesus.aguaapp.notificaciones.Notificador
import corona.jesus.aguaapp.ui.theme.AguaAppTheme
import corona.jesus.aguaapp.viewmodels.AguaViewModel
import corona.jesus.aguaapp.vistas.PrincipalScreen

class MainActivity : ComponentActivity() {
    private val viewModel: AguaViewModel by viewModels()
    private lateinit var notificador: Notificador
    private val pedirPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            enviarRecordatorio()
        } else {
            Toast.makeText(this, R.string.permiso_necesario, Toast.LENGTH_LONG).show()
        }
    }

    private fun tienePermiso(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun revisarPermisoYEnviar() {
        if (tienePermiso()) {
            enviarRecordatorio()
        } else {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun enviarRecordatorio() {
        notificador.mostrar()
        viewModel.agregarRegistro()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificador = Notificador(this)
        notificador.crearCanal()
        enableEdgeToEdge()
        setContent {
            AguaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PrincipalScreen(
                        viewModel = viewModel,
                        onBoton = { revisarPermisoYEnviar() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}