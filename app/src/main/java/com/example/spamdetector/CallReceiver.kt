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
            val numeroEntrante = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            val mensaje = if (!numeroEntrante.isNullOrEmpty()) {
                "📞 Llamada entrante de: $numeroEntrante"
            } else {
                "📞 Llamada entrante detectada"
            }

            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }
}
