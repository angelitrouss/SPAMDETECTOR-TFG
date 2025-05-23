package com.ejemplo.spamdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
                // No intentamos acceder al número para evitar errores
                Toast.makeText(context, "📞 Llamada entrante detectada", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("CallReceiver", "Error al procesar la llamada: ${e.message}")
            Toast.makeText(context, "Error al detectar la llamada", Toast.LENGTH_SHORT).show()
        }
    }
}
