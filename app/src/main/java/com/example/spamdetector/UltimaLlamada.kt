package com.example.spamdetector

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object UltimaLlamada {
    var llamada by mutableStateOf<Llamada?>(null)
}
