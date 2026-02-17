package app.zornslemma.mypricelog.feature.nfceimport

import java.nio.file.Files
import java.nio.file.Paths
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class NfceHtmlParserTest {
    @Test
    fun parseFixture() {
        val html =
            String(
                Files.readAllBytes(Paths.get("src/test/resources/nfce/sample_sp_nfce.html"))
            )
        val receipt = NfceHtmlParser().parse("https://example", html, "key")
        assertEquals("Mercado Paulista", receipt.storeName)
        assertEquals(2, receipt.items.size)
        assertTrue(receipt.items.first().description.contains("ARROZ"))
    }
}
