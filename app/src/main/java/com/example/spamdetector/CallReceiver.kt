package com.example.spamdetector

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                CoroutineScope(Dispatchers.IO).launch {
                    val esSpam = numero?.let { SpamChecker.esSpam(it) } ?: false

                    val llamada = Llamada(
                        numero = numero,
                        fechaHora = fechaHora,
                        esSpam = esSpam
                    )

                    UltimaLlamada.llamada = llamada
                    HistorialLlamadas.agregarLlamada(context, llamada)

                    mostrarNotificacion(context, numero ?: "Desconocido", esSpam)
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

        val builder = NotificationCompat.Builder(context, "canal_llamada")
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1001, builder.build())
            Log.d("CALL_RECEIVER", "Notificación mostrada")
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
}
