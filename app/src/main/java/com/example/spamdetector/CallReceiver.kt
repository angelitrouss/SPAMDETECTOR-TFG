package com.example.spamdetector

import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CALL_RECEIVER", "Intent recibido")

        val action = intent.action
        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            Log.d("CALL_RECEIVER", "Estado de llamada: $estado")

            if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
                val numero = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val numeroFormateado = numero ?: "Desconocido"
                val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                Log.d("CALL_RECEIVER", "Número detectado: $numeroFormateado")

                abrirApp(context) // 👈 Abrir la app al recibir llamada

                // Forzar recomposición en la UI reseteando la llamada anterior
                UltimaLlamada.llamada = null

                SpamChecker.esSpam(numeroFormateado) { esSpam ->
                    Log.d("CALL_RECEIVER", "Resultado spam para $numeroFormateado: $esSpam")

                    val llamada = Llamada(
                        numero = numeroFormateado,
                        fechaHora = fechaHora,
                        esSpam = esSpam
                    )

                    UltimaLlamada.llamada = llamada
                    HistorialLlamadas.agregarLlamada(context, llamada)
                    mostrarNotificacion(context, numeroFormateado, esSpam)
                }
            }
        } else {
            Log.d("CALL_RECEIVER", "Intent no relacionado con PHONE_STATE")
        }
    }

    private fun mostrarNotificacion(context: Context, numero: String, esSpam: Boolean) {
        crearCanalDeNotificacion(context)

        val titulo = if (esSpam) "⚠️ Sospechoso de SPAM" else "📞 Llamada entrante"
        val mensaje = if (esSpam) "Número sospechoso: $numero" else "Llamada de: $numero"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "canal_llamada")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_view,
                "Abrir SpamDetector",
                pendingIntent
            )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, builder.build())
            Log.d("CALL_RECEIVER", "Notificación mostrada con acceso a la app")
        } else {
            Log.d("CALL_RECEIVER", "Permiso POST_NOTIFICATIONS no concedido")
        }
    }

    private fun crearCanalDeNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "canal_llamada",
                "Llamadas entrantes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificación para llamadas detectadas"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal)
        }
    }

    private fun abrirApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
        Log.d("CALL_RECEIVER", "App lanzada desde llamada entrante")
    }
}