package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val permisoTelefono = mutableStateOf(false)
    private val permisoNotificaciones = mutableStateOf(false)

    private lateinit var solicitarPermisosLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registrar launcher para solicitar múltiples permisos
        solicitarPermisosLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultado ->
            permisoTelefono.value =
                resultado[Manifest.permission.READ_PHONE_STATE] == true

            permisoNotificaciones.value =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        resultado[Manifest.permission.POST_NOTIFICATIONS] == true

            // Puedes agregar un mensaje visual aquí si quieres notificar al usuario
        }

        // Cargar historial antes de mostrar la interfaz (asegúrate de que esta función maneje permisos)
        HistorialLlamadas.cargarHistorial(this)

        // Revisar permisos actuales
        actualizarEstadosDePermisos()

        // Mostrar interfaz principal
        setContent {
            PantallaPrincipal(
                permisoTelefono = permisoTelefono,
                permisoNotificacion = permisoNotificaciones,
                onSolicitarPermisos = { solicitarPermisos() }
            )
        }

        // Pedir permisos si faltan (esto debería ir después de UI para evitar errores de timing)
        if (!permisoTelefono.value || !permisoNotificaciones.value) {
            solicitarPermisos()
        }
    }

    private fun solicitarPermisos() {
        val permisos = mutableListOf(Manifest.permission.READ_PHONE_STATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        solicitarPermisosLauncher.launch(permisos.toTypedArray())
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
