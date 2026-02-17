package app.zornslemma.mypricelog.feature.nfceimport

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request

class NfceHtmlFetcher {
    private val client =
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .callTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    fun fetch(url: String): String {
        val request =
            Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Android) MyPriceLog/0.2")
                .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("HTTP ${response.code}")
            return response.body?.string() ?: error("Empty response")
        }
    }
}
