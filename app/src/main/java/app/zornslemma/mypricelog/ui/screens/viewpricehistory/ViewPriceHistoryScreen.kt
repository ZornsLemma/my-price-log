@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.screens.viewpricehistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.PriceHistory
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.components.CardTitle
import app.zornslemma.mypricelog.ui.components.LabeledItem
import app.zornslemma.mypricelog.ui.components.MyDropdownMenuItem
import app.zornslemma.mypricelog.ui.components.OverflowMenu
import app.zornslemma.mypricelog.ui.components.PackPriceAndSizeRow
import app.zornslemma.mypricelog.ui.components.topAppBarTitle
import app.zornslemma.mypricelog.ui.screenHorizontalBorder
import app.zornslemma.mypricelog.ui.screenVerticalBorder
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle

@Composable
fun ViewPriceHistoryScreen(
    viewModel: ViewPriceHistoryViewModel,
    requestClose: () -> Unit,
    requestEditAsNew: (priceHistory: PriceHistory) -> Unit,
) {
    val dataSet = viewModel.uiContent.staticContent.dataSet
    val item = viewModel.uiContent.staticContent.item
    val source = viewModel.uiContent.staticContent.source
    val price = viewModel.uiContent.staticContent.price

    val locale = LocalConfiguration.current.locales[0]
    val zoneId = ZoneId.systemDefault()
    val confirmedAtFormatter =
        remember(locale, zoneId) {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(locale)
                .withZone(zoneId)
        }
    val priceHistoryList by viewModel.priceHistoryListFlow.collectAsStateWithLifecycle(emptyList())
    val priceHistoryDeltaList =
        remember(priceHistoryList, locale) {
            viewModel.generatePriceHistoryDeltaList(priceHistoryList, locale, confirmedAtFormatter)
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { requestClose() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                        )
                    }
                },
                title = topAppBarTitle(item.name, source.name),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = screenHorizontalBorder, vertical = screenVerticalBorder)
        ) {
            val dateFormatter =
                remember(locale, zoneId) {
                    // I don't know if this is the best way to do it or if it might have subtle
                    // localisation concerns, but I really do want to have the day of the week in
                    // there and FormatStyle.LONG (which is the only FormatStyle to include the day
                    // of the week) is too long. The idea here is that the day of the week might
                    // help to make it easier to connect to memories of going to the shop for recent
                    // visits.
                    DateTimeFormatterBuilder()
                        .appendPattern("EEE")
                        .appendLiteral(" ")
                        .append(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                        .toFormatter(locale)
                        .withZone(zoneId)
                }

            val timeFormatter =
                remember(locale, zoneId) {
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                        .withLocale(locale)
                        .withZone(zoneId)
                }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(priceHistoryDeltaList) { index, priceHistoryDelta ->
                    if (priceHistoryDelta == null) {
                        HorizontalDividerWithText(stringResource(R.string.label_deleted))
                    } else {
                        Box {
                            ItemSourceInfoHistory(
                                dataSet,
                                priceHistoryDelta,
                                dateFormatter,
                                timeFormatter,
                            )

                            // ENHANCE: It would be good to allow deleting history entries
                            // (irreversibly of course) from this menu. This would allow "perfect"
                            // cleanup of accidental edits (e.g. editing a price for store X when
                            // you wanted store Y - you could restore the pre-edit price using
                            // edit-as-new and then delete the mistaken edit)
                            OverflowMenu(modifier = Modifier.align(Alignment.TopEnd)) {
                                requestMenuClose ->
                                // We don't allow "Edit as new price" on the first item - this is
                                // the current price and should be edited via the home screen
                                // instead. If we allow this, the user can bypass the usual check
                                // for no-op edits and create duplicate entries in the history
                                // table. (This can be seen in the database but is hidden by the
                                // diffing process in the history screen.) We could special case
                                // this (by not setting the nonLinearEdit flag when editing the
                                // first item) but it seems best just to disallow it. The exception
                                // is if there is no current price (because it was deleted), in
                                // which case it's reasonable to edit the latest historical price as
                                // new.
                                MyDropdownMenuItem(
                                    text = {
                                        Text(stringResource(R.string.menu_item_edit_as_new_price))
                                    },
                                    enabled = price == null || index > 0,
                                    onClick = {
                                        requestMenuClose()
                                        requestEditAsNew(priceHistoryDelta.priceHistory)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HorizontalDividerWithText(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.CenterVertically)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun ItemSourceInfoHistory(
    dataSet: DataSet,
    priceHistoryDelta: PriceHistoryDelta,
    modifiedAtTitleFormatter: DateTimeFormatter,
    modifiedAtSubtitleFormatter: DateTimeFormatter,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
        ) {
            CardTitle(
                title = modifiedAtTitleFormatter.format(priceHistoryDelta.modifiedAt),
                subtitle = modifiedAtSubtitleFormatter.format(priceHistoryDelta.modifiedAt),
            )

            if (
                priceHistoryDelta.price != null ||
                    priceHistoryDelta.count != null ||
                    priceHistoryDelta.quantity != null
            ) {
                myCheck(
                    priceHistoryDelta.price != null &&
                        priceHistoryDelta.count != null &&
                        priceHistoryDelta.quantity != null
                ) {
                    "Expected price, count and quantity to all be non-null since one is"
                }
                PackPriceAndSizeRow(
                    priceHistoryDelta.price!!,
                    priceHistoryDelta.count!!,
                    priceHistoryDelta.quantity!!,
                    dataSet,
                    AsyncOperationStatus.Idle,
                )
            }

            if (priceHistoryDelta.confirmedAt != null) {
                LabeledItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    label = stringResource(R.string.label_confirmed),
                ) {
                    Text(priceHistoryDelta.confirmedAt)
                }
            }

            if (priceHistoryDelta.notes != null) {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    LabeledItem(stringResource(R.string.label_notes)) {
                        Text(priceHistoryDelta.notes)
                    }
                }
            }
        }
    }
}
