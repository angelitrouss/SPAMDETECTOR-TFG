package com.example.spamdetector

data class Llamada(
    val numero: String?,
    val fechaHora: String,
    val esSpam: Boolean = false
)
