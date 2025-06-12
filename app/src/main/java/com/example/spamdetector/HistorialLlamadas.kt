package com.example.spamdetector

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HistorialLlamadas {
    private const val PREFS_NAME = "historial_prefs"
    private const val CLAVE_HISTORIAL = "historial_llamadas"
    private val historial = mutableListOf<Llamada>()

    fun agregarLlamada(context: Context, llamada: Llamada) {
        historial.add(0, llamada)
        if (historial.size > 10) {
            historial.removeAt(historial.lastIndex)
        }
        guardarHistorial(context)
        Log.d("HISTORIAL", "Llamada agregada: $llamada")
    }

    fun obtenerHistorial(): List<Llamada> {
        return historial.toList()
    }

    fun cargarHistorial(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(CLAVE_HISTORIAL, null)
        if (!json.isNullOrEmpty()) {
            val tipoLista = object : TypeToken<List<Llamada>>() {}.type
            val lista = Gson().fromJson<List<Llamada>>(json, tipoLista)
            historial.clear()
            historial.addAll(lista)
            Log.d("HISTORIAL", "Historial cargado: ${historial.size} llamadas")
        } else {
            Log.d("HISTORIAL", "Historial vacío")
        }
    }

    private fun guardarHistorial(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(historial)
        editor.putString(CLAVE_HISTORIAL, json)
        editor.apply()
        Log.d("HISTORIAL", "Historial guardado (${historial.size}) llamadas")
    }
}
