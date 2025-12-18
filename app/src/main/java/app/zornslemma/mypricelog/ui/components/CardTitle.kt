package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardTitle(title: String, subtitle: String? = null) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
    if (subtitle != null) {
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
    }
    Spacer(modifier = Modifier.height(8.dp))
}
