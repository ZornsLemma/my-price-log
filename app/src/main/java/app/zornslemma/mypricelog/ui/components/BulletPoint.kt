package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.zornslemma.mypricelog.ui.bulletPoint

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("$bulletPoint ", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}
