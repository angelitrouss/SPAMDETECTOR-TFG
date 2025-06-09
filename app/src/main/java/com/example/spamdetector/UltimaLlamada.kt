package com.example.spamdetector

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class Llamada(
    val numero: String?,
    val fechaHora: String,
    val esSpam: Boolean = false // 🔍 Nuevo campo para marcar llamadas como spam
)

object UltimaLlamada {
    var llamada by mutableStateOf<Llamada?>(null)
}
