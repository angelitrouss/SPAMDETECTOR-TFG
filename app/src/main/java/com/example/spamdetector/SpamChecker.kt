package com.example.spamdetector

import com.google.firebase.firestore.FirebaseFirestore

object SpamChecker {

    private val db = FirebaseFirestore.getInstance()

    fun esSpam(numero: String, callback: (Boolean) -> Unit) {
        db.collection("numerosSpam")
            .whereEqualTo("numero", numero)
            .get()
            .addOnSuccessListener { documentos ->
                callback(!documentos.isEmpty)
            }
            .addOnFailureListener { error ->
                error.printStackTrace()
                callback(false)
            }
    }
}
