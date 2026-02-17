package app.zornslemma.mypricelog.feature.nfceimport

import java.math.BigDecimal
import java.time.Instant

data class Receipt(
    val accessKey: String?,
    val sourceUrl: String,
    val storeName: String?,
    val cnpj: String?,
    val datetime: Instant?,
    val items: List<ReceiptItem>,
)

data class ReceiptItem(
    val description: String,
    val qty: Double?,
    val unitText: String?,
    val unitPrice: BigDecimal?,
    val totalPrice: BigDecimal?,
    val productCode: String?,
)

data class NfceResolvedPayload(val url: String, val accessKey: String?)

data class NfceImportSummary(
    val importedCount: Int,
    val createdItems: Int,
    val updatedPrices: Int,
    val skippedCount: Int,
    val storeName: String?,
    val datetimeText: String,
)
