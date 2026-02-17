package app.zornslemma.mypricelog.feature.nfceimport

import android.content.Context
import android.content.Intent

object NfceImportEntry {
    fun createIntent(context: Context): Intent = Intent(context, NfceImportActivity::class.java)
}
