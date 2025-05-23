package com.ejemplo.spamdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (estado == TelephonyManager.EXTRA_STATE_RINGING) {
            // Mostramos un mensaje básico sin intentar acceder al número
            Toast.makeText(
                context,
                "📞 Llamada entrante detectada",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
