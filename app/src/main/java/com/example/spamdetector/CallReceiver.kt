package com.example.spamdetector

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

    override fun onReceive(context: Context, intent: Intent) {
        val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
            crearCanalDeNotificacion(context)

            val builder = NotificationCompat.Builder(context, "canal_llamada")
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .setContentTitle("📞 Llamada entrante")
                .setContentText("Tu app ha detectado una llamada.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {

                NotificationManagerCompat.from(context).notify(1001, builder.build())
            }
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
