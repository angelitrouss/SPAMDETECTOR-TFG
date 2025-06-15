package com.example.spamdetector

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object MarcadorDeSpam {

    private val db = FirebaseFirestore.getInstance()

    fun marcarComoSpam(numero: String, onResultado: (Boolean) -> Unit) {
        val numeroLimpio = numero.trim()

        if (numeroLimpio == "Número oculto") {
            Log.w("MarcadorDeSpam", "⚠️ No se puede marcar un número oculto como SPAM")
            onResultado(false)
            return
        }

        db.collection("numerosSpam")
            .whereEqualTo("numero", numeroLimpio)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    val data = hashMapOf("numero" to numeroLimpio)
                    db.collection("numerosSpam")
                        .add(data)
                        .addOnSuccessListener {
                            Log.d("MarcadorDeSpam", "✅ Número $numeroLimpio agregado como SPAM")
                            onResultado(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("MarcadorDeSpam", "❌ Error al guardar número como SPAM", e)
                            onResultado(false)
                        }
                } else {
                    Log.d("MarcadorDeSpam", "⚠️ Número $numeroLimpio ya estaba marcado como SPAM")
                    onResultado(true)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MarcadorDeSpam", "❌ Error al consultar Firestore", e)
                onResultado(false)
            }
    }

    fun eliminarDeSpam(numero: String, onResultado: (Boolean) -> Unit) {
        val numeroLimpio = numero.trim()

        if (numeroLimpio == "Número oculto") {
            Log.w("MarcadorDeSpam", "⚠️ No se puede eliminar un número oculto de SPAM")
            onResultado(false)
            return
        }

        db.collection("numerosSpam")
            .whereEqualTo("numero", numeroLimpio)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    Log.d("MarcadorDeSpam", "ℹ️ El número $numeroLimpio no estaba en Firestore")
                    onResultado(false)
                } else {
                    val batch = db.batch()
                    documentos.forEach { doc -> batch.delete(doc.reference) }
                    batch.commit()
                        .addOnSuccessListener {
                            Log.d("MarcadorDeSpam", "✅ Número $numeroLimpio eliminado de SPAM")
                            onResultado(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("MarcadorDeSpam", "❌ Error al eliminar número de Firestore", e)
                            onResultado(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("MarcadorDeSpam", "❌ Error al consultar Firestore", e)
                onResultado(false)
            }
    }
}
