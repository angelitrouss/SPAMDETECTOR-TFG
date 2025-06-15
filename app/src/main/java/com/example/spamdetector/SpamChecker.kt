package com.example.spamdetector

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object SpamChecker {

    private val db = FirebaseFirestore.getInstance()

    fun esSpam(numero: String, callback: (Boolean) -> Unit) {
        val numeroLimpio = numero.trim()

        // No comprobar spam para números ocultos
        if (numeroLimpio == "Número oculto") {
            Log.w("SpamChecker", "⚠️ Número oculto recibido, no se puede comprobar SPAM.")
            callback(false)
            return
        }

        db.collection("numerosSpam")
            .whereEqualTo("numero", numeroLimpio)
            .get()
            .addOnSuccessListener { documentos ->
                val esSpam = !documentos.isEmpty
                Log.d("SpamChecker", "📡 Comprobación para $numeroLimpio → ¿SPAM? $esSpam")
                callback(esSpam)
            }
            .addOnFailureListener { error ->
                Log.e("SpamChecker", "❌ Error consultando Firestore para $numeroLimpio", error)
                callback(false)
            }
    }
}
