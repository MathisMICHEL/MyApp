package com.example.myapp

import java.net.HttpURLConnection
import java.net.URL

// Function to fetch Wikipedia summary in a background thread
fun fetchWikipediaSummary(entityName: String, onResult: (String) -> Unit) {
    val urlString = "https://mathismichel.pythonanywhere.com/wiki?entity=$entityName"

    Thread {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }

                // Return the summary back to the caller (onResult)
                onResult(response)
            } else {
                onResult("Failed to fetch summary")
            }
        } catch (e: Exception) {
            onResult("Error: ${e.message}")
        }
    }.start()
}

fun processSummary(summary: String): String {
    return summary.substring(10,summary.length-14)
        .replace(Regex("""\\n([a-zA-Z])"""), " $1")
        .replace("\n", " ")
        .replace(Regex("""\[\d+]"""), "")
        .replace(Regex("""\\(?!n)"""), "")
        .replace("\"", "")
        .replace(Regex("""\s+"""), " ")
        .trim()
}
