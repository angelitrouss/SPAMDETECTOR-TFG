package com.ejemplo.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar y solicitar permiso
        val permiso = Manifest.permission.READ_PHONE_STATE
        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permiso), REQUEST_CODE)
        }

        // Mostrar la interfaz
        setContent {
            Greeting("Angel")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hola $name, app funcionando 😊")
}
