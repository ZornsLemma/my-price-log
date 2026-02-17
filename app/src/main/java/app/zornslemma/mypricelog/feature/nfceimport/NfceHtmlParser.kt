package app.zornslemma.mypricelog.feature.nfceimport

import org.jsoup.Jsoup

class NfceHtmlParser {
    fun parse(sourceUrl: String, html: String, accessKey: String?): Receipt {
        val doc = Jsoup.parse(html)
        val wholeText = doc.text()

        val storeName =
            doc.selectFirst("#u20, .txtTopo, .txtCenter")?.text()?.takeIf { it.isNotBlank() }
                ?: regexGroup(wholeText, "(?:Emitente|Loja)[:\\s]+([^\\n]+)")
        val cnpj = regexGroup(wholeText, "CNPJ[:\\s]+([0-9./-]{14,18})")
        val datetime =
            NfceParsing.parseDateTime(regexGroup(wholeText, "Data/Hora[:\\s]+([0-9/: ]{16,19})"))

        val items = parseItemsFromDom(doc) + parseItemsFromTextFallback(wholeText)
        val deduped = items.distinctBy { listOf(it.description, it.unitPrice, it.totalPrice, it.qty) }

        return Receipt(
            accessKey = accessKey,
            sourceUrl = sourceUrl,
            storeName = storeName,
            cnpj = cnpj,
            datetime = datetime,
            items = deduped,
        )
    }

    private fun parseItemsFromDom(doc: org.jsoup.nodes.Document): List<ReceiptItem> {
        val rows = doc.select("tr[id*=Item]")
        if (rows.isEmpty()) return emptyList()
        return rows.mapNotNull { row ->
            val text = row.text()
            val description =
                row.selectFirst(".txtTit, .txtTit2")?.text()?.let { NfceParsing.normalizeDescription(it) }
                    ?: regexGroup(text, "^(.*?)\\s+Qtde\\.")?.let { NfceParsing.normalizeDescription(it) }
            if (description.isNullOrBlank()) return@mapNotNull null
            ReceiptItem(
                description = description,
                qty = NfceParsing.parseQty(regexGroup(text, "Qtde\\.:\\s*([0-9.,]+)")),
                unitText = NfceParsing.normalizeUnitText(regexGroup(text, "UN[:\\s]+([A-Za-zÇç]{1,5})")),
                unitPrice =
                    NfceParsing.parseBrlNumber(
                        regexGroup(text, "Vl\\. Unit\\.:\\s*R?\\$?\\s*([0-9.,]+)")
                    ),
                totalPrice =
                    NfceParsing.parseBrlNumber(
                        regexGroup(text, "Vl\\. Total\\s*R?\\$?\\s*([0-9.,]+)")
                    ),
                productCode = regexGroup(text, "(?:C[oó]digo|EAN)[:\\s]+([A-Za-z0-9]+)"),
            )
        }
    }

    private fun parseItemsFromTextFallback(text: String): List<ReceiptItem> {
        val pattern =
            Regex(
                "(.*?)\\s+Qtde\\.:\\s*([0-9.,]+).*?UN[:\\s]+([A-Za-zÇç]{1,5}).*?Vl\\. Unit\\.:\\s*R?\\$?\\s*([0-9.,]+).*?Vl\\. Total\\s*R?\\$?\\s*([0-9.,]+)",
                setOf(RegexOption.IGNORE_CASE),
            )
        return pattern.findAll(text).mapNotNull { match ->
            val description = NfceParsing.normalizeDescription(match.groupValues[1])
            if (description.isBlank()) return@mapNotNull null
            ReceiptItem(
                description = description,
                qty = NfceParsing.parseQty(match.groupValues[2]),
                unitText = NfceParsing.normalizeUnitText(match.groupValues[3]),
                unitPrice = NfceParsing.parseBrlNumber(match.groupValues[4]),
                totalPrice = NfceParsing.parseBrlNumber(match.groupValues[5]),
                productCode = null,
            )
        }.toList()
    }

    private fun regexGroup(text: String, pattern: String): String? {
        return Regex(pattern, setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(text)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
    }
}
