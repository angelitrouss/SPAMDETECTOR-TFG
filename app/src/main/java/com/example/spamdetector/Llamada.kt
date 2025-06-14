package com.example.spamdetector

import java.util.UUID

data class Llamada(
    val numero: String?,
    val fechaHora: String,
    val esSpam: Boolean = false,
    val id: String = UUID.randomUUID().toString() // ID único para forzar actualización
)

