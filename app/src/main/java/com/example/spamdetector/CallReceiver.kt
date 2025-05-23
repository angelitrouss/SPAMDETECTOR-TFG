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

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CALL_RECEIVER", "Intent recibido")

        // Verificamos que sea una acción del sistema para llamada
        val action = intent.action
        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            Log.d("CALL_RECEIVER", "Estado de llamada: $estado")

            if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
                crearCanalDeNotificacion(context)

                val builder = NotificationCompat.Builder(context, "canal_llamada")
                    .setSmallIcon(android.R.drawable.sym_call_incoming)
                    .setContentTitle("📞 Llamada entrante")
                    .setContentText("SpamDetector esta detectando una llamada.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {

                    NotificationManagerCompat.from(context).notify(1001, builder.build())
                    Log.d("CALL_RECEIVER", "Notificación mostrada")
                } else {
                    Log.d("CALL_RECEIVER", "Permiso POST_NOTIFICATIONS no concedido")
                }
            }
        } else {
            Log.d("CALL_RECEIVER", "Intent no relacionado con PHONE_STATE")
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