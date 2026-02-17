package app.zornslemma.mypricelog.feature.nfceimport

import org.junit.Test
import org.junit.Assert.assertEquals

class NfceImportRepositoryTest {
    @Test
    fun importKeyUsesAccessKeyAndFallsBackToHash() {
        val withKey =
            Receipt("abc", "https://x", "s", null, null, emptyList())
        val noKey =
            Receipt(null, "https://x", "s", null, null, emptyList())

        assertEquals("abc", withKey.accessKey ?: NfceParsing.hashUrl(withKey.sourceUrl))
        assertEquals(NfceParsing.hashUrl("https://x"), noKey.accessKey ?: NfceParsing.hashUrl(noKey.sourceUrl))
    }
}
