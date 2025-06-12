package com.example.spamdetector

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object MarcadorDeSpam {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Guarda un número en Firestore como spam.
     * Si ya existe, no lo duplica.
     */
    fun marcarComoSpam(numero: String, onResultado: (Boolean) -> Unit) {
        db.collection("numerosSpam")
            .whereEqualTo("numero", numero)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    // No existe, lo agregamos
                    val data = hashMapOf("numero" to numero)
                    db.collection("numerosSpam")
                        .add(data)
                        .addOnSuccessListener {
                            Log.d("MarcadorDeSpam", "Número $numero agregado como spam")
                            onResultado(true)
                        }
                        .addOnFailureListener {
                            Log.e("MarcadorDeSpam", "Error al guardar número", it)
                            onResultado(false)
                        }
                } else {
                    Log.d("MarcadorDeSpam", "Número $numero ya existe en Firestore")
                    onResultado(true) // Ya estaba marcado
                }
            }
            .addOnFailureListener {
                Log.e("MarcadorDeSpam", "Error al buscar número", it)
                onResultado(false)
            }
    }

    /**
     * Elimina un número de la lista de spam en Firestore.
     */
    fun eliminarDeSpam(numero: String, onResultado: (Boolean) -> Unit) {
        db.collection("numerosSpam")
            .whereEqualTo("numero", numero)
            .get()
            .addOnSuccessListener { documentos ->
                if (!documentos.isEmpty) {
                    val batch = db.batch()
                    for (doc in documentos) {
                        batch.delete(doc.reference)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            Log.d("MarcadorDeSpam", "Número $numero eliminado de Firestore")
                            onResultado(true)
                        }
                        .addOnFailureListener {
                            Log.e("MarcadorDeSpam", "Error al eliminar número", it)
                            onResultado(false)
                        }
                } else {
                    Log.d("MarcadorDeSpam", "Número $numero no estaba en Firestore")
                    onResultado(false)
                }
            }
            .addOnFailureListener {
                Log.e("MarcadorDeSpam", "Error al buscar número", it)
                onResultado(false)
            }
    }
}
