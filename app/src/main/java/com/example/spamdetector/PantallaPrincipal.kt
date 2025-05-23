package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun PantallaPrincipal(onSolicitarPermisos: () -> Unit) {
    val context = LocalContext.current

    // Variables con estado reactivo
    var permisoTelefonoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permisoNotificacionesConcedido by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Efecto que se ejecuta cada vez que vuelve la pantalla activa (como al aceptar permisos)
    LaunchedEffect(Unit) {
        permisoTelefonoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        permisoNotificacionesConcedido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📞 SpamDetector", style = MaterialTheme.typography.headlineSmall)

        HorizontalDivider()

        Text(text = "Estado de permisos:", style = MaterialTheme.typography.bodyLarge)
        Text("📱 Teléfono: ${if (permisoTelefonoConcedido) "Concedido ✅" else "No concedido ❌"}")
        Text("🔔 Notificaciones: ${if (permisoNotificacionesConcedido) "Concedido ✅" else "No concedido ❌"}")

        Button(onClick = {
            onSolicitarPermisos()

            // Refrescar el estado justo después de pedirlos
            permisoTelefonoConcedido = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED

            permisoNotificacionesConcedido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
        }) {
            Text("Solicitar permisos manualmente")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("📋 Próximamente: listado de llamadas y bloqueo de spam", style = MaterialTheme.typography.bodySmall)
    }
}
