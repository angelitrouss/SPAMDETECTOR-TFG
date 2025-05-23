package com.ejemplo.spamdetector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CallReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
            // Intentar obtener el número
            val numero = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            // Crear canal de notificación
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canal = NotificationChannel(
                    "canal_llamada",
                    "Llamadas entrantes",
                    NotificationManager.IMPORTANCE_HIGH
                )
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(canal)
            }

            // Definir el mensaje
            val mensaje = if (!numero.isNullOrEmpty()) {
                "📞 Llamada de: $numero"
            } else {
                "📞 Llamada entrante detectada"
            }

            // Crear notificación
            val builder = NotificationCompat.Builder(context, "canal_llamada")
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .setContentTitle("Llamada entrante")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            NotificationManagerCompat.from(context).notify(1001, builder.build())
        }
    }
}
