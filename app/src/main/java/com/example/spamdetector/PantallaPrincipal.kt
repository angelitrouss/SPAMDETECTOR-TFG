package com.example.spamdetector

import androidx.compose.animation.*
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

@Composable
fun PantallaPrincipal(
    permisoTelefono: State<Boolean>,
    permisoNotificacion: State<Boolean>,
    onSolicitarPermisos: () -> Unit
) {
    val ultimaLlamada = UltimaLlamada.llamada
    val historial = HistorialLlamadas.obtenerHistorial()
    val context = LocalContext.current

    var mensajeExito by remember { mutableStateOf<String?>(null) }

    val numeroClave = ultimaLlamada?.numero

    val (yaMarcadoComoSpam, setYaMarcadoComoSpam) = remember(numeroClave) {
        mutableStateOf<Boolean?>(null)
    }

    LaunchedEffect(numeroClave) {
        numeroClave?.let { numero ->
            setYaMarcadoComoSpam(null)
            SpamChecker.esSpam(numero.trim()) { esSpam ->
                setYaMarcadoComoSpam(esSpam)
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
            text = "\uD83D\uDCDE SpamDetector",
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

        AnimatedVisibility(
            visible = ultimaLlamada != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5FB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("\uD83D\uDCF2 Última llamada", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    ultimaLlamada?.let { llamada ->
                        val numero = llamada.numero ?: "Número oculto"

                        Text("• Número: $numero", fontSize = 14.sp)
                        Text("\uD83D\uDD52 ${llamada.fechaHora}", fontSize = 13.sp, color = Color.DarkGray)

                        if (yaMarcadoComoSpam == null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Verificando si es spam...", fontSize = 14.sp, color = Color.DarkGray)
                            }
                        } else {
                            val textoSpam = if (yaMarcadoComoSpam == true || llamada.esSpam)
                                "\u26A0\uFE0F SPAM SOSPECHOSO" else "\u2705 No es spam"

                            Text(
                                textoSpam,
                                fontWeight = FontWeight.SemiBold,
                                color = if (yaMarcadoComoSpam == true || llamada.esSpam)
                                    Color.Red else Color(0xFF4CAF50)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (yaMarcadoComoSpam == false && !llamada.esSpam) {
                            Button(
                                onClick = {
                                    MarcadorDeSpam.marcarComoSpam(numero) { exito ->
                                        if (exito) {
                                            mensajeExito = "Número $numero marcado como SPAM"
                                            SpamChecker.esSpam(numero.trim()) { esSpam ->
                                                setYaMarcadoComoSpam(esSpam)
                                            }
                                        } else {
                                            mensajeExito = "Error al marcar como SPAM"
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Marcar como SPAM")
                            }
                        } else if (yaMarcadoComoSpam == true || llamada.esSpam) {
                            Button(
                                onClick = {
                                    MarcadorDeSpam.eliminarDeSpam(numero) { exito ->
                                        if (exito) {
                                            mensajeExito = "Número $numero eliminado de la lista de SPAM"
                                            SpamChecker.esSpam(numero.trim()) { esSpam ->
                                                setYaMarcadoComoSpam(esSpam)
                                            }
                                        } else {
                                            mensajeExito = "Error al eliminar de SPAM"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Quitar de SPAM")
                            }
                        }

                        mensajeExito?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        if (ultimaLlamada == null) {
            Text("📭 Esperando nueva llamada...", color = Color.Gray, fontSize = 14.sp)
        }

        Button(
            onClick = { onSolicitarPermisos() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Solicitar permisos")
        }

        Text(
            "\uD83D\uDCDC Historial de llamadas",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (historial.isEmpty()) {
            Text("Aún no hay historial disponible", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(historial) { llamada ->
                    val iconColor = if (llamada.esSpam) Color.Red else Color(0xFF4CAF50)
                    val icon = if (llamada.esSpam) Icons.Default.Warning else Icons.Default.Call
                    val fondo = if (llamada.esSpam) Color(0xFFFFEBEE) else Color.White

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = fondo)
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
                                Icon(icon, contentDescription = null, tint = Color.White)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                val numero = llamada.numero ?: "Número oculto"
                                Text(numero, fontWeight = FontWeight.SemiBold)
                                Text(llamada.fechaHora, fontSize = 12.sp, color = Color.DarkGray)

                                if (llamada.esSpam) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .background(Color.Red, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "\u26A0\uFE0F SPAM SOSPECHOSO",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
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
    val esDesconocido = llamada.numero?.startsWith("Desconocido") == true
    val iconColor = when {
        llamada.esSpam -> Color.Red
        esDesconocido -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }
    val icon = when {
        llamada.esSpam -> Icons.Default.Warning
        esDesconocido -> Icons.Default.Phone // puedes cambiar por otro icono si lo prefieres
        else -> Icons.Default.Call
    }

    val fondo = when {
        llamada.esSpam -> Color(0xFFFFEBEE)
        esDesconocido -> Color(0xFFE3F2FD)
        else -> Color.White
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
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
                Icon(icon, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                val numero = llamada.numero ?: "Desconocido"
                Text(
                    numero,
                    fontWeight = FontWeight.SemiBold
                )
                Text(llamada.fechaHora, fontSize = 12.sp, color = Color.DarkGray)

                if (llamada.esSpam) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(Color.Red, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "\u26A0\uFE0F SPAM SOSPECHOSO",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                if (esDesconocido) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(Color(0xFF2196F3), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔒 Número oculto",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
