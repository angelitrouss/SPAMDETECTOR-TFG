package com.example.spamdetector

import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class CallReceiver : BroadcastReceiver() {

    private val NOTIF_ID = 1001
    private val CANAL_ID = "canal_llamada"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CALL_RECEIVER", "Intent recibido")

        val action = intent.action
        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            Log.d("CALL_RECEIVER", "Estado de llamada: $estado")

            when (estado) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    val rawNumero = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                    var numero = rawNumero

                    if (numero.isNullOrBlank()) {
                        numero = obtenerNumeroDesdeRegistro(context)
                    }

                    if (numero.isNullOrBlank()) {
                        numero = "Número oculto"
                    }

                    Log.d("CALL_RECEIVER", "Número detectado: $numero")

                    mostrarNotificacionInicial(context, numero)
                    UltimaLlamada.llamada = null

                    SpamChecker.esSpam(numero) { esSpam ->
                        Log.d("CALL_RECEIVER", "Resultado spam para $numero: $esSpam")

                        val llamada = Llamada(
                            numero = numero,
                            fechaHora = fechaHora,
                            esSpam = esSpam
                        )

                        UltimaLlamada.llamada = llamada
                        HistorialLlamadas.agregarLlamada(context, llamada)
                        actualizarNotificacion(context, numero, esSpam)
                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d("CALL_RECEIVER", "Llamada finalizada → limpiando UI y notificación")
                    UltimaLlamada.llamada = null
                    NotificationManagerCompat.from(context).cancel(NOTIF_ID)
                }
            }
        }
    }

    private fun obtenerNumeroDesdeRegistro(context: Context): String? {
        try {
            val cursor = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.DATE),
                null,
                null,
                CallLog.Calls.DATE + " DESC"
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val numero = it.getString(it.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                    Log.d("CALL_RECEIVER", "Número recuperado del registro: $numero")
                    return numero
                }
            }
        } catch (e: Exception) {
            Log.e("CALL_RECEIVER", "Error al acceder al registro de llamadas", e)
        }
        return null
    }

    private fun mostrarNotificacionInicial(context: Context, numero: String) {
        crearCanalDeNotificacion(context)

        val mensaje = "Detectando si es SPAM..."
        val titulo = "📞 Llamada de: $numero"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIF_ID, builder.build())
            Log.d("CALL_RECEIVER", "Notificación inicial mostrada")
        }
    }

    private fun actualizarNotificacion(context: Context, numero: String, esSpam: Boolean) {
        val titulo = if (esSpam) "⚠️ Sospechoso de SPAM" else "📞 Llamada entrante"
        val mensaje = if (esSpam) "Número sospechoso: $numero" else "Llamada de: $numero"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "Abrir SpamDetector", pendingIntent)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(NOTIF_ID, builder.build())
            Log.d("CALL_RECEIVER", "Notificación actualizada con resultado SPAM=$esSpam")
        }
    }

    private fun crearCanalDeNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
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
