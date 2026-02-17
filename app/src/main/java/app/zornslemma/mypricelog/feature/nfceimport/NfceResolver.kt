package app.zornslemma.mypricelog.feature.nfceimport

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object NfceResolver {
    private const val SP_HOST = "www.nfce.fazenda.sp.gov.br"
    private const val SP_QR_PATH_1 = "/qrcode"
    private const val SP_QR_PATH_2 = "/NFCeConsultaPublica/Paginas/ConsultaQRCode.aspx"
    private const val SP_BASE_URL = "https://$SP_HOST$SP_QR_PATH_2"
    private val accessKeyRegex = Regex("\\b\\d{44}\\b")

    fun resolve(rawPayload: String): NfceResolvedPayload? {
        val cleaned = rawPayload.trim().replace(Regex("\\s+"), "")
        if (cleaned.isBlank()) return null

        val candidateUrl =
            when {
                cleaned.startsWith("http://", ignoreCase = true) ||
                    cleaned.startsWith("https://", ignoreCase = true) -> cleaned
                cleaned.contains("p=", ignoreCase = true) -> "$SP_BASE_URL?$cleaned"
                else -> return null
            }

        val parsed = runCatching { URI(candidateUrl) }.getOrNull() ?: return null
        val host = parsed.host?.lowercase() ?: return null
        if (host != SP_HOST) return null

        val path = parsed.path ?: ""
        if (path != SP_QR_PATH_1 && path != SP_QR_PATH_2) return null

        return NfceResolvedPayload(url = parsed.toString(), accessKey = extractAccessKey(parsed.toString()))
    }

    fun extractAccessKey(url: String): String? {
        val query = runCatching { URI(url).rawQuery }.getOrNull().orEmpty()
        query.split("&").forEach { part ->
            if (part.startsWith("p=", ignoreCase = true)) {
                val decoded = URLDecoder.decode(part.substringAfter('='), StandardCharsets.UTF_8.name())
                val token = decoded.substringBefore('|')
                if (token.matches(Regex("\\d{44}"))) return token
            }
        }
        return accessKeyRegex.find(url)?.value
    }
}
