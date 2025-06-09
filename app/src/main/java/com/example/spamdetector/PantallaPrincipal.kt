package com.example.spamdetector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaPrincipal(
    permisoTelefono: State<Boolean>,
    permisoNotificacion: State<Boolean>,
    onSolicitarPermisos: () -> Unit
) {
    val ultimaLlamada = UltimaLlamada.llamada

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ENCABEZADO
        Text(
            text = "📞 SpamDetector",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // TARJETAS DE PERMISOS
        PermisoCard("📱 Permiso Teléfono", permisoTelefono.value)
        PermisoCard("🔔 Permiso Notificaciones", permisoNotificacion.value)

        // SECCIÓN DE ÚLTIMA LLAMADA
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFFE0F7FA), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("📲 Última llamada detectada", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (ultimaLlamada != null) {
                    val numero = ultimaLlamada.numero ?: "Desconocido"
                    val textoSpam = if (ultimaLlamada.esSpam) "⚠️ SPAM SOSPECHOSO" else "✅ No es spam"

                    Text("• Número: $numero", fontSize = 14.sp)
                    Text("🕒 ${ultimaLlamada.fechaHora}", fontSize = 13.sp, color = Color.DarkGray)
                    Text(textoSpam, fontWeight = FontWeight.SemiBold, color = if (ultimaLlamada.esSpam) Color.Red else Color(0xFF4CAF50))
                } else {
                    Text("Aún no hay llamadas registradas", color = Color.DarkGray)
                }
            }
        }

        // BOTÓN DE PERMISOS
        Button(
            onClick = { onSolicitarPermisos() },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text("Solicitar permisos manualmente")
        }

        // SECCIÓN DE HISTORIAL (placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFFF3E5F5), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("📜 Historial de llamadas (próximamente)", fontWeight = FontWeight.Bold)
                Text("Aquí aparecerá la lista con duración y estado", color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun PermisoCard(titulo: String, concedido: Boolean) {
    val colorFondo = if (concedido) Color(0xFFDFF0D8) else Color(0xFFF2DEDE)
    val colorTexto = if (concedido) Color(0xFF3C763D) else Color(0xFFA94442)
    val estadoTexto = if (concedido) "Concedido ✅" else "No concedido ❌"

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(colorFondo, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(titulo, fontWeight = FontWeight.Bold, color = colorTexto)
            Text(estadoTexto, color = colorTexto)
        }
    }
}
