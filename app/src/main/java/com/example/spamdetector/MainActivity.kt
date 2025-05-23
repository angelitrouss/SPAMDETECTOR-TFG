package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PantallaPrincipal { solicitarPermisos() }
        }

        solicitarPermisos()
    }

    private fun solicitarPermisos() {
        val permisos = mutableListOf(Manifest.permission.READ_PHONE_STATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisos.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val permisosNoConcedidos = permisos.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permisosNoConcedidos.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permisosNoConcedidos.toTypedArray(),
                REQUEST_CODE
            )
        }
    }
}
