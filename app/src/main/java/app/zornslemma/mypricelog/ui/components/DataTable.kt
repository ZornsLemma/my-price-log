package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.zornslemma.mypricelog.debug.myRequire
import app.zornslemma.mypricelog.ui.oneLineListItemHeight

enum class CellAlignment {
    Start,
    Center,
    End,
}

// ENHANCE: I suspect this is going to need some tweaking to give even a half-decent experience with
// a screen reader.
@Composable
fun <T> DataTable(
    header: List<String>,
    headerTextModifiers: List<Modifier>? = null,
    items: List<T>,
    columns: List<@Composable (T) -> Unit>,
    highlightRow: Int? = null,
    columnWeights: List<Float> = List(header.size) { 1f },
    columnAlignments: List<CellAlignment> = List(header.size) { CellAlignment.Start },
    onClick: ((T) -> Unit)? = null,
) {
    myRequire(header.size == columns.size) {
        "Expected same header and columns size but have ${header.size} and ${columns.size} respectively"
    }
    myRequire(header.size == columnWeights.size) {
        "Expected same header and columnWeights size but have ${header.size} and ${columnWeights.size} respectively"
    }
    myRequire(header.size == columnAlignments.size) {
        "Expected same header and columnAlignments size but have ${header.size} and ${columnAlignments.size} respectively"
    }

    fun alignmentModifier(cellAlignment: CellAlignment): Modifier =
        when (cellAlignment) {
            CellAlignment.Start -> Modifier.wrapContentWidth(Alignment.Start)
            CellAlignment.Center -> Modifier.wrapContentWidth(Alignment.CenterHorizontally)
            CellAlignment.End -> Modifier.wrapContentWidth(Alignment.End)
        }

    Column {
        // ENHANCE: If we allow user-selectable units in this header via a dropdown, its height may
        // need increasing to oneLineListItemHeight.
        Row(
            modifier =
                Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHighest),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            header.forEachIndexed { colIndex, title ->
                Box(
                    Modifier.weight(columnWeights[colIndex])
                        .padding(8.dp)
                        .then(alignmentModifier(columnAlignments[colIndex]))
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.then(headerTextModifiers?.get(colIndex) ?: Modifier),
                    )
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline /* Variant */)

        items.forEachIndexed { rowIndex, item ->
            val isHighlighted = rowIndex == highlightRow
            val textStyle =
                if (isHighlighted) {
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                } else {
                    MaterialTheme.typography.bodyLarge
                }
            val textColor =
                if (isHighlighted) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            val rowBackground =
                if (isHighlighted) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                }

            CompositionLocalProvider(
                LocalTextStyle provides textStyle,
                LocalContentColor provides textColor,
            ) {
                Row(
                    modifier =
                        Modifier.background(rowBackground)
                            .height(oneLineListItemHeight)
                            .then(
                                if (onClick != null) Modifier.clickable { onClick(item) }
                                else Modifier
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    columns.forEachIndexed { colIndex, cell ->
                        Box(
                            Modifier.weight(columnWeights[colIndex])
                                .padding(8.dp)
                                .then(alignmentModifier(columnAlignments[colIndex]))
                        ) {
                            cell(item)
                        }
                    }
                }
            }
        }
    }
}
