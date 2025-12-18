package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun <T, ID : Comparable<ID>> RadioButtonGroup(
    title: String,
    items: List<T>,
    enabled: Boolean,
    selectedId: ID?,
    onItemSelected: (ID) -> Unit,
    getId: (T) -> ID,
    getNameResource: (T) -> Int,
    getSupportingTextResource: (T) -> Int?,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        items.forEach { item ->
            val clickableModifier =
                if (!enabled) Modifier else Modifier.clickable { onItemSelected(getId(item)) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxWidth()
                        .then(clickableModifier)
                        .padding(horizontal = 8.dp)
                        .height(
                            48.dp
                        ) // 40.dp is MD3 spec but we want extra space for our supporting text while
                        // still having some spacing between items
                        .semantics {
                            role = Role.RadioButton
                        }, // for TalkBack / screen readers, since this is clickable not the
                // RadioButton
            ) {
                RadioButton(
                    selected = getId(item) == selectedId,
                    enabled = enabled,
                    onClick = null, // the enclosing Row is clickable instead
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = stringResource(getNameResource(item)))
                    getSupportingTextResource(item)?.let { supportingTextResource ->
                        Text(
                            text = stringResource(supportingTextResource),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
