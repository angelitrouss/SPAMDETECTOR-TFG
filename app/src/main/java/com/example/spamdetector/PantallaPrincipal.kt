package com.example.spamdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


@Composable
fun PantallaPrincipal(onSolicitarPermisos: () -> Unit) {
    val context = LocalContext.current

    val tienePermisoTelefono = ContextCompat.checkSelfPermission(
        context, Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

    val tienePermisoNotificaciones =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

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

        Text("📱 Teléfono: ${if (tienePermisoTelefono) "Concedido ✅" else "No concedido ❌"}")
        Text("🔔 Notificaciones: ${if (tienePermisoNotificaciones) "Concedido ✅" else "No concedido ❌"}")

        Button(onClick = { onSolicitarPermisos() }) {
            Text("Solicitar permisos manualmente")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("📋 Próximamente: listado de llamadas y bloqueo de spam", style = MaterialTheme.typography.bodySmall)
    }
}
