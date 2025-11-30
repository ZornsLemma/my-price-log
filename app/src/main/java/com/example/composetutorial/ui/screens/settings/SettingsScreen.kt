@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial.ui.screens.settings

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.composetutorial.R
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.components.WarningIcon
import com.example.composetutorial.domain.defaultAncientPriceThresholdDays
import com.example.composetutorial.domain.defaultAnnualInflationPercent
import com.example.composetutorial.domain.defaultStalePriceThreshold
import com.example.composetutorial.ui.common.failedValidationRuleOrNull
import com.example.composetutorial.ui.common.UiText
import com.example.composetutorial.ui.screenVerticalBorder
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavHostController,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val stalePriceThreshold by viewModel.settingsRepository.stalePriceThresholdFlow.collectAsStateWithLifecycle(initialValue = defaultStalePriceThreshold)
    val ancientPriceThresholdDays by viewModel.settingsRepository.ancientPriceThresholdDaysFlow.collectAsStateWithLifecycle(initialValue =defaultAncientPriceThresholdDays)
    val annualInflationPercent by viewModel.settingsRepository.annualInflationPercentFlow.collectAsStateWithLifecycle(initialValue = defaultAnnualInflationPercent)
    var showStalePriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAncientPriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAnnualInflationPercentDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_settings)) }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.content_description_back)
                    )
                }
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                //.background(MaterialTheme.colorScheme.primary) // TODO: debug hack
                .fillMaxSize()
                .padding(innerPadding)
                // Padding here follows the same approach as GeneralSelectorScreen() - see the
                // comment there.
                .padding(vertical = screenVerticalBorder)
                .verticalScroll(rememberScrollState())
        ) {
            // ENHANCE: Since stale price threshold and ancient price threshold have interrelated
            // validation, there just might be an argument for allowing them to be edited
            // simultaneously to help avoid the annoyance of wanting to change one and having to
            // cancel out and go change the other first. This is probably not a huge deal in
            // practice. Editing these should not be an everyday activity so even if it is a bit
            // fiddly it doesn't matter that much.

            // TODO: I half wonder if we should do *<=* stale/ancient threshold - currently we use < in code for stale at least - just to match this description (subtitle) which feels more natural, but maybe we can reword this.

            SettingsTile(
                title = stringResource(R.string.title_stale_price_threshold),
                subtitle = pluralStringResource(
                    R.plurals.supporting_text_prices_considered_stale_after_x_days,
                    count = stalePriceThreshold,stalePriceThreshold
                ),
                onClick = {
                    showStalePriceThresholdDialog = true
                }
            )

            SettingsTile(
                title = stringResource(R.string.title_ancient_price_threshold),
                subtitle = pluralStringResource(
                    R.plurals.supporting_text_prices_considered_ancient_after_x_days,
                    count = ancientPriceThresholdDays, ancientPriceThresholdDays
                ),
                onClick = {
                    showAncientPriceThresholdDialog = true
                }
            )

            SettingsTile(
                title = stringResource(R.string.title_annual_inflation),
                // Ancient prices increase in the same way too, but it's probably best to keep the
                // subtitle simple here rather than being over-precise.
                subtitle = stringResource(
                    R.string.supporting_text_stale_prices_increase_by_percent_per_year,
                    annualInflationPercent
                ),
                onClick = {
                    showAnnualInflationPercentDialog = true
                }
            )

            SettingsTile(
                title = stringResource(R.string.title_backup),
                subtitle = stringResource(R.string.supporting_text_back_up_your_data_to_a_file),
                onClick = onBackupClick
            )

            SettingsTile(
                title = stringResource(R.string.title_restore),
                subtitle = stringResource(R.string.supporting_text_replace_all_data_with_a_backup),
                onClick = { showRestoreConfirmDialog = true }
            )

            SettingsTile(
                title = stringResource(R.string.title_about_app_name),
                subtitle = "", // empty subtitle gives consistent layout with other tiles
                onClick = onAboutClick
            )
        }

        if (showStalePriceThresholdDialog) {
            SettingsDialog(
                title = stringResource(R.string.title_stale_price_threshold),
                subtitle = stringResource(R.string.supporting_text_stale_price_threshold),
                label = stringResource(R.string.title_stale_price_threshold),
                // ENHANCE: Can we make all (not just this one) uses of suffix_days pluralise
                // correctly based on the current value (if it can be parsed as an integer; I guess
                // default to pluralising-as-if it is 99 or something otherwise?)?
                suffix = { Text(stringResource(R.string.suffix_days)) },
                initialValue = stalePriceThreshold.toString(),
                validationRules = listOfNotNull(
                    ValidationRule({ it.trim().isNotEmpty() }, UiText.Res(R.string.supporting_text_required)),
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days >= 1
                        },
                        UiText.Res(R.string.supporting_text_must_be_positive)
                    ),
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days < ancientPriceThresholdDays
                        },
                        UiText.Res(
                            R.string.supporting_text_must_be_less_than_x_ancient_price_threshold,
                            listOf(ancientPriceThresholdDays)
                        )
                    )
                ),
                onConfirm = { stalePriceThresholdString ->
                    showStalePriceThresholdDialog = false
                    viewModel.settingsRepository.setStalePriceThresholdAsync(stalePriceThresholdString.toInt())
                },
                onDismissRequest = {
                    showStalePriceThresholdDialog = false
                }
            )
        }

        if (showAncientPriceThresholdDialog) {
            SettingsDialog(
                title = stringResource(R.string.title_ancient_price_threshold),
                subtitle = stringResource(R.string.supporting_text_ancient_price_threshold),
                label = stringResource(R.string.title_ancient_price_threshold),
                suffix = { Text(stringResource(R.string.suffix_days)) },
                initialValue = ancientPriceThresholdDays.toString(),
                validationRules = listOfNotNull(
                    ValidationRule({ it.trim().isNotEmpty() }, UiText.Res(R.string.supporting_text_required)),
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days > stalePriceThreshold
                        },
                        UiText.Res(
                            R.string.supporting_text_must_be_greater_than_x_stale_price_threshold,
                            listOf(stalePriceThreshold)
                        )
                    ),
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days <= 365
                        },
                        UiText.Res(R.string.supporting_text_must_be_no_greater_than_365)
                    ),
                ),
                onConfirm = { ancientPriceThresholdDaysString ->
                    showAncientPriceThresholdDialog = false
                    viewModel.settingsRepository.setAncientPriceThresholdDaysAsync(
                        ancientPriceThresholdDaysString.toInt())
                },
                onDismissRequest = {
                    showAncientPriceThresholdDialog = false
                }
            )
        }

        if (showAnnualInflationPercentDialog) {
            SettingsDialog(
                title = stringResource(R.string.title_annual_inflation),
                subtitle = stringResource(R.string.supporting_text_annual_inflation),
                label = stringResource(R.string.title_annual_inflation),
                suffix = { Text("%") },
                initialValue = annualInflationPercent.toString(),
                validationRules = listOfNotNull(
                    ValidationRule({ it.trim().isNotEmpty() }, UiText.Res(R.string.supporting_text_required)),
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null
                        },
                        UiText.Res(R.string.supporting_text_must_be_a_whole_number),
                    ),
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null && inflation >= 0
                        },
                        UiText.Res(R.string.supporting_text_must_be_zero_or_greater)
                    ),
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null && inflation <= 1000
                        },
                        UiText.Res(R.string.supporting_text_must_be_no_greater_than_1000)
                    ),
                ),
                onConfirm = { annualInflationPercentString ->
                    showAnnualInflationPercentDialog = false
                    viewModel.settingsRepository.setAnnualInflationPercentAsync(
                        annualInflationPercentString.toInt())
                },
                onDismissRequest = {
                    showAnnualInflationPercentDialog = false
                }
            )
        }

        if (showRestoreConfirmDialog) {
            // ENHANCE: I don't want to overdo it - but this is both destructive and rare - but
            // should we show another final "are you sure?" dialog after the user has chosen a file
            // inside onRestoreClick() before we actually go ahead? I think it is still good to have
            // a dialog at this point, as it immediately makes it obvious if the user mis-tapped on
            // restore when they meant to tap on backup - both of which would otherwise go straight
            // into a system file selection dialog.
            AlertDialog(
                icon = { WarningIcon(contentDescription = stringResource(R.string.content_description_warning)) },
                title = { Text(stringResource(R.string.title_restore_from_backup)) },
                text = { Text(stringResource(R.string.message_restore_from_backup_warning)) },
                onDismissRequest = { showRestoreConfirmDialog = false },
                dismissButton = {
                    TextButton(onClick = { showRestoreConfirmDialog = false }) { Text(stringResource(R.string.button_cancel)) }
                },
                confirmButton = {
                    TextButton(onClick = { showRestoreConfirmDialog = false; onRestoreClick() }) { Text(
                        stringResource(R.string.button_restore)
                    ) }
                }
            )
        }
    }
}

@Composable
private fun SettingsTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

// Our full screen edit dialogs always have "Save" enabled but show warnings if a mandatory field is
// empty only after you've tried to save for the first time. SettingsDialogs behave differently -
// "Save" is simply disabled when there's an error or the value is empty. I think this is fine,
// because here there is a single text field so the user's attention is naturally focused on it,
// unlike a full screen dialog with multiple editable fields.
//
// TODO: Can/should this have a small lag in updating supportingText as I think our normal full
// screen edit dialogs did at one point (not sure if they still do)?
@Composable
private fun SettingsDialog(
    title: String,
    subtitle: String,
    label: String,
    initialValue: String,
    validationRules: List<ValidationRule<String>>,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
    suffix: @Composable (() -> Unit)? = null,
) {
    var currentValue by rememberSaveable { mutableStateOf(initialValue) }
    var textFieldValue by remember { mutableStateOf(
        TextFieldValue(
            text = currentValue,
            // Put the caret at the end of the string - this is why we need a TextFieldValue.
            selection = TextRange(currentValue.length))) }
    var error by remember { mutableStateOf<UiText?>(null) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                Text(subtitle, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it; currentValue = it.text; error = failedValidationRuleOrNull(validationRules, it.text.trim())?.message },
                    label = { Text(label) },
                    suffix = suffix,
                    supportingText = {
                        if (error != null) Text(
                            error!!.asString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (error == null) {
                        onConfirm(currentValue.trim())
                    }
                },
                enabled = currentValue.trim().isNotEmpty() && error == null
            ) { Text(stringResource(R.string.button_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.button_cancel)) }
        }
    )

    LaunchedEffect(Unit) {
        // This delay is a ChatGPT-suggested magic value to let the dialog animation complete before showing the keyboard. Apparently some versions of Android may not show the keyboard if focus is requested before this point.
        delay(150)
        focusRequester.requestFocus()
    }

}
