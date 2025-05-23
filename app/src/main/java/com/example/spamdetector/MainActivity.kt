package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        solicitarPermisos()

        setContent {
            TextoInicio()
        }
    }

    private fun solicitarPermisos() {
        // Lista de permisos necesarios
        val permisos = mutableListOf(Manifest.permission.READ_PHONE_STATE)

        // A partir de Android 13 también pedimos permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Comprobamos cuáles no han sido concedidos
        val permisosNoConcedidos = permisos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        // Si hay alguno pendiente, los solicitamos
        if (permisosNoConcedidos.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permisosNoConcedidos.toTypedArray(),
                REQUEST_CODE
            )
        }
    }
}

@Composable
fun TextoInicio() {
    Text(text = "App en funcionamiento. Esperando llamadas telefónicas 📞")
}
