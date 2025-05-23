package com.example.spamdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Asegurarse de que el evento recibido es del tipo PHONE_STATE
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {

            val estadoLlamada = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (estadoLlamada == TelephonyManager.EXTRA_STATE_RINGING) {
                val numeroEntrante = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                if (!numeroEntrante.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        "📞 Llamada entrante de: $numeroEntrante",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
