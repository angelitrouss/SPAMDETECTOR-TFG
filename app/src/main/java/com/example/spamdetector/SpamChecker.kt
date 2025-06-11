package com.example.spamdetector

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object SpamChecker {

    suspend fun esSpam(numero: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://spam-backend-siqx.onrender.com/es_spam/$numero")  // Cambia si usas IP distinta
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 3000
                conn.readTimeout = 3000

                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    return@withContext json.optBoolean("es_spam", false)
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
}
