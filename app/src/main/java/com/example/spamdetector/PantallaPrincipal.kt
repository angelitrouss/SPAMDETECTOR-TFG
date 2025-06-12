package com.example.spamdetector

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PantallaPrincipal(
    permisoTelefono: State<Boolean>,
    permisoNotificacion: State<Boolean>,
    onSolicitarPermisos: () -> Unit
) {
    val ultimaLlamada = UltimaLlamada.llamada
    val historial = HistorialLlamadas.obtenerHistorial()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var yaMarcadoComoSpam by remember { mutableStateOf(false) }

    // Si hay llamada, consultamos si ya fue marcada como spam
    LaunchedEffect(ultimaLlamada?.numero) {
        ultimaLlamada?.numero?.let { numero ->
            SpamChecker.esSpam(numero) { esSpam ->
                yaMarcadoComoSpam = esSpam
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "📞 SpamDetector",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PermisoChip("Teléfono", permisoTelefono.value, Icons.Default.Phone)
            PermisoChip("Notificación", permisoNotificacion.value, Icons.Default.Notifications)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5FB))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📲 Última llamada", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (ultimaLlamada != null) {
                    val numero = ultimaLlamada.numero ?: "Desconocido"
                    val textoSpam = if (ultimaLlamada.esSpam) "⚠️ SPAM SOSPECHOSO" else "✅ No es spam"

                    Text("• Número: $numero", fontSize = 14.sp)
                    Text("🕒 ${ultimaLlamada.fechaHora}", fontSize = 13.sp, color = Color.DarkGray)
                    Text(
                        textoSpam,
                        fontWeight = FontWeight.SemiBold,
                        color = if (ultimaLlamada.esSpam) Color.Red else Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón solo si aún no está marcado
                    if (!yaMarcadoComoSpam) {
                        Button(
                            onClick = {
                                MarcadorDeSpam.marcarComoSpam(numero) { exito ->
                                    if (exito) {
                                        mensajeExito = "Número $numero marcado como SPAM"
                                        yaMarcadoComoSpam = true
                                    } else {
                                        mensajeExito = "Error al marcar como SPAM"
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Marcar como SPAM")
                        }
                    }

                    mensajeExito?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, fontSize = 12.sp, color = Color.DarkGray)
                    }

                } else {
                    Text("Aún no hay llamadas registradas", color = Color.DarkGray)
                }
            }
        }

        Button(
            onClick = { onSolicitarPermisos() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Solicitar permisos")
        }

        Text(
            "📜 Historial de llamadas",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (historial.isEmpty()) {
            Text("Aún no hay historial disponible", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(historial) { llamada ->
                    LlamadaItem(llamada)
                }
            }
        }
    }
}

@Composable
fun PermisoChip(nombre: String, concedido: Boolean, icono: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (concedido) Color(0xFFDEFDE0) else Color(0xFFFFE0E0),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(icono, contentDescription = null, tint = if (concedido) Color(0xFF2E7D32) else Color(0xFFC62828))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (concedido) "$nombre ✅" else "$nombre ❌",
                color = if (concedido) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LlamadaItem(llamada: Llamada) {
    val iconColor = if (llamada.esSpam) Color.Red else Color(0xFF4CAF50)
    val icon = if (llamada.esSpam) Icons.Default.Warning else Icons.Default.Call

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (llamada.esSpam) Color(0xFFFFEBEE) else Color.White
        )

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                val numero = llamada.numero ?: "Desconocido"
                val estado = if (llamada.esSpam) "SPAM" else "Normal"

                Text(numero, fontWeight = FontWeight.SemiBold)
                Text("${llamada.fechaHora} — $estado", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}
