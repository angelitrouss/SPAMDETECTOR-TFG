package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.spamdetector.PantallaPrincipal

class MainActivity : ComponentActivity() {

    // Variables de estado que se actualizan automáticamente
    private val permisoTelefono = mutableStateOf(false)
    private val permisoNotificaciones = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Actualiza el estado de los permisos al arrancar
        actualizarEstadosDePermisos()

        setContent {
            PantallaPrincipal(
                permisoTelefono = permisoTelefono,
                permisoNotificacion = permisoNotificaciones,
                onSolicitarPermisos = { solicitarPermisos() }
            )
        }
    }

    private fun solicitarPermisos() {
        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisosResultado ->
            permisoTelefono.value =
                permisosResultado[Manifest.permission.READ_PHONE_STATE] == true

            permisoNotificaciones.value =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        permisosResultado[Manifest.permission.POST_NOTIFICATIONS] == true
        }

        val permisos = mutableListOf(Manifest.permission.READ_PHONE_STATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        launcher.launch(permisos.toTypedArray())
    }

    private fun actualizarEstadosDePermisos() {
        permisoTelefono.value = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        permisoNotificaciones.value = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }
}
