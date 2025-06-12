package com.example.spamdetector

import com.google.firebase.firestore.FirebaseFirestore

object SpamChecker {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Consulta Firestore para verificar si un número está registrado como spam.
     * Llama al callback con true si es spam, false si no lo es o si hay error.
     */
    fun esSpam(numero: String, callback: (Boolean) -> Unit) {
        db.collection("numerosSpam")
            .whereEqualTo("numero", numero)
            .get()
            .addOnSuccessListener { documentos ->
                callback(!documentos.isEmpty)
            }
            .addOnFailureListener { error ->
                error.printStackTrace()
                callback(false) // En caso de error, tratamos como "no spam"
            }
    }
}
