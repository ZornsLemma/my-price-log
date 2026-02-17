package app.zornslemma.mypricelog.feature.nfceimport

import app.zornslemma.mypricelog.domain.MeasurementUnit
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

class NfceParsingTest {
    @Test
    fun parseBrl() {
        assertEquals("1234.56", NfceParsing.parseBrlNumber("R$ 1.234,56")?.toPlainString())
        assertEquals("1.19", NfceParsing.parseBrlNumber("1,19")?.toPlainString())
    }

    @Test
    fun detectPackSize() {
        val detected = NfceParsing.detectPackSize("Molho 500ML")
        assertNotNull(detected)
        assertEquals(500L, detected.first)
        assertEquals(MeasurementUnit.ML, detected.second)
    }

    @Test
    fun extractAccessKey() {
        val key = "35123456789012345678901234567890123456789012"
        val url = "https://www.nfce.fazenda.sp.gov.br/qrcode?p=${key}|2|1|abc"
        assertEquals(key, NfceResolver.extractAccessKey(url))
    }
}
