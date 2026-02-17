package app.zornslemma.mypricelog.feature.nfceimport

import app.zornslemma.mypricelog.domain.MeasurementUnit
import java.math.BigDecimal
import java.security.MessageDigest
import java.text.Normalizer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object NfceParsing {
    private val brlCleanerRegex = Regex("[^0-9,.-]")

    fun normalizeDescription(value: String): String {
        val normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
        return normalized
            .replace(Regex("[\\p{Cntrl}&&[^\\n\\t]]"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    fun parseBrlNumber(raw: String?): BigDecimal? {
        val source = raw?.trim()?.ifBlank { null } ?: return null
        val cleaned = source.replace(brlCleanerRegex, "")
        val normalized = cleaned.replace(".", "").replace(',', '.')
        return normalized.toBigDecimalOrNull()?.takeIf { it >= BigDecimal.ZERO }
    }

    fun parseQty(raw: String?): Double? {
        val source = raw?.trim()?.ifBlank { null } ?: return null
        val normalized =
            when {
                source.contains(',') && source.contains('.') -> source.replace(".", "").replace(',', '.')
                source.contains(',') -> source.replace(',', '.')
                else -> source
            }
        return normalized.toDoubleOrNull()?.takeIf { it >= 0.0 }
    }

    fun parseDateTime(raw: String?): Instant? {
        val source = raw?.trim()?.ifBlank { null } ?: return null
        val patterns =
            listOf(
                "dd/MM/yyyy HH:mm:ss",
                "dd/MM/yyyy HH:mm",
            )
        patterns.forEach { pattern ->
            val formatter = DateTimeFormatter.ofPattern(pattern)
            runCatching {
                    LocalDateTime.parse(source, formatter)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                }
                .getOrNull()
                ?.let { return it }
        }
        return null
    }

    fun detectPackSize(description: String): Pair<Double, MeasurementUnit>? {
        val d = description.uppercase(Locale.ROOT)
        val match = Regex("(\\d+(?:[.,]\\d+)?)\\s*(KG|G|L|LT|ML)").find(d) ?: return null
        val value = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return null
        return when (match.groupValues[2]) {
            "KG" -> (value * 1000.0) to MeasurementUnit.KG
            "G" -> value to MeasurementUnit.G
            "L", "LT" -> (value * 1000.0) to MeasurementUnit.L
            "ML" -> value to MeasurementUnit.ML
            else -> null
        }
    }

    fun detectMultipack(description: String): Long {
        val text = description.uppercase(Locale.ROOT)
        return Regex("(?:CX\\s*)?(\\d{1,3})\\s*(UN|UND|PC)").find(text)?.groupValues?.get(1)?.toLongOrNull()
            ?: Regex("(\\d{1,3})\\s*[X×]").find(text)?.groupValues?.get(1)?.toLongOrNull()
            ?: 1L
    }

    fun normalizeUnitText(unitText: String?): String? {
        val normalized = unitText?.trim()?.uppercase(Locale.ROOT) ?: return null
        return when (normalized) {
            "L", "LT", "LTR" -> "L"
            "KG", "K" -> "KG"
            "UN", "UND", "PC", "PÇ", "PCE" -> "UN"
            else -> normalized
        }
    }

    fun hashUrl(url: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(url.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
