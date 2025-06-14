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
    private val permisoCallLog = mutableStateOf(false)

    private lateinit var solicitarPermisosLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Registrar launcher para solicitar múltiples permisos
        solicitarPermisosLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { resultado ->
            permisoTelefono.value =
                resultado[Manifest.permission.READ_PHONE_STATE] == true

            permisoCallLog.value =
                resultado[Manifest.permission.READ_CALL_LOG] == true

            permisoNotificaciones.value =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        resultado[Manifest.permission.POST_NOTIFICATIONS] == true

            // Si se obtuvo el permiso, cargar historial
            if (permisoTelefono.value && permisoCallLog.value) {
                HistorialLlamadas.cargarHistorial(this)
            }
        }

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

        // Pedir permisos si faltan
        if (!permisoTelefono.value || !permisoCallLog.value || !permisoNotificaciones.value) {
            solicitarPermisos()
        } else {
            HistorialLlamadas.cargarHistorial(this)
        }
    }

    private fun solicitarPermisos() {
        val permisos = mutableListOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        solicitarPermisosLauncher.launch(permisos.toTypedArray())
    }

    private fun actualizarEstadosDePermisos() {
        permisoTelefono.value = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        permisoCallLog.value = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED

        permisoNotificaciones.value = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }
}
