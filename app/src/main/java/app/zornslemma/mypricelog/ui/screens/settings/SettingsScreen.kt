@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.domain.defaultAncientPriceThresholdDays
import app.zornslemma.mypricelog.domain.defaultAnnualInflationPercent
import app.zornslemma.mypricelog.domain.defaultStalePriceThresholdDays
import app.zornslemma.mypricelog.ui.common.UiText
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.components.NumericTextField
import app.zornslemma.mypricelog.ui.components.WarningIcon
import app.zornslemma.mypricelog.ui.components.textOrNull
import app.zornslemma.mypricelog.ui.components.validateFieldState
import app.zornslemma.mypricelog.ui.screenVerticalBorder
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavHostController,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val stalePriceThresholdDays by
        viewModel.settingsRepository.stalePriceThresholdDaysFlow.collectAsStateWithLifecycle(
            initialValue = defaultStalePriceThresholdDays
        )
    val ancientPriceThresholdDays by
        viewModel.settingsRepository.ancientPriceThresholdDaysFlow.collectAsStateWithLifecycle(
            initialValue = defaultAncientPriceThresholdDays
        )
    val annualInflationPercent by
        viewModel.settingsRepository.annualInflationPercentFlow.collectAsStateWithLifecycle(
            initialValue = defaultAnnualInflationPercent
        )
    var showStalePriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAncientPriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAnnualInflationPercentDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    // Padding here follows the same approach as GeneralSelectorScreen() - see the
                    // comment there.
                    .padding(vertical = screenVerticalBorder)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
        ) {
            // ENHANCE: Since stale price threshold and ancient price threshold have interrelated
            // validation, there just might be an argument for allowing them to be edited
            // simultaneously to help avoid the annoyance of wanting to change one and having to
            // cancel out and go change the other first. This is probably not a huge deal in
            // practice. Editing these should not be an everyday activity so even if it is a bit
            // fiddly it doesn't matter that much.

            SettingsTile(
                title = stringResource(R.string.title_stale_price_threshold),
                subtitle =
                    pluralStringResource(
                        R.plurals.supporting_text_prices_considered_stale_after_x_days,
                        count = stalePriceThresholdDays,
                        stalePriceThresholdDays,
                    ),
                onClick = { showStalePriceThresholdDialog = true },
            )

            SettingsTile(
                title = stringResource(R.string.title_ancient_price_threshold),
                subtitle =
                    pluralStringResource(
                        R.plurals.supporting_text_prices_considered_ancient_after_x_days,
                        count = ancientPriceThresholdDays,
                        ancientPriceThresholdDays,
                    ),
                onClick = { showAncientPriceThresholdDialog = true },
            )

            SettingsTile(
                title = stringResource(R.string.title_annual_inflation),
                // Ancient prices increase in the same way too, but it's probably best to keep the
                // subtitle simple here rather than being over-precise.
                subtitle =
                    stringResource(
                        R.string.supporting_text_stale_prices_increase_by_percent_per_year,
                        annualInflationPercent,
                    ),
                onClick = { showAnnualInflationPercentDialog = true },
            )

            SettingsTile(
                title = stringResource(R.string.title_backup),
                subtitle = stringResource(R.string.supporting_text_back_up_your_data_to_a_file),
                onClick = onBackupClick,
            )

            SettingsTile(
                title = stringResource(R.string.title_restore),
                subtitle = stringResource(R.string.supporting_text_replace_all_data_with_a_backup),
                onClick = { showRestoreConfirmDialog = true },
            )

            SettingsTile(
                title = stringResource(R.string.title_about_app_name),
                subtitle = "", // empty subtitle gives consistent layout with other tiles
                onClick = onAboutClick,
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
                initialValue = stalePriceThresholdDays.toString(),
                validationRules =
                    listOfNotNull(
                        ValidationRule(
                            { it.trim().isNotEmpty() },
                            UiText.Res(R.string.supporting_text_required),
                        ),
                        ValidationRule(
                            {
                                val days = it.toIntOrNull()
                                days != null && days >= 1
                            },
                            UiText.Res(R.string.supporting_text_must_be_positive),
                        ),
                        ValidationRule(
                            {
                                val days = it.toIntOrNull()
                                days != null && days < ancientPriceThresholdDays
                            },
                            UiText.Res(
                                R.string
                                    .supporting_text_must_be_less_than_x_ancient_price_threshold,
                                listOf(ancientPriceThresholdDays),
                            ),
                        ),
                    ),
                onConfirm = { stalePriceThresholdDaysString ->
                    showStalePriceThresholdDialog = false
                    viewModel.settingsRepository.setStalePriceThresholdAsync(
                        stalePriceThresholdDaysString.toInt()
                    )
                },
                onDismissRequest = { showStalePriceThresholdDialog = false },
            )
        }

        if (showAncientPriceThresholdDialog) {
            SettingsDialog(
                title = stringResource(R.string.title_ancient_price_threshold),
                subtitle = stringResource(R.string.supporting_text_ancient_price_threshold),
                label = stringResource(R.string.title_ancient_price_threshold),
                suffix = { Text(stringResource(R.string.suffix_days)) },
                initialValue = ancientPriceThresholdDays.toString(),
                validationRules =
                    listOfNotNull(
                        ValidationRule(
                            { it.trim().isNotEmpty() },
                            UiText.Res(R.string.supporting_text_required),
                        ),
                        ValidationRule(
                            {
                                val days = it.toIntOrNull()
                                days != null && days > stalePriceThresholdDays
                            },
                            UiText.Res(
                                R.string
                                    .supporting_text_must_be_greater_than_x_stale_price_threshold,
                                listOf(stalePriceThresholdDays),
                            ),
                        ),
                        ValidationRule(
                            {
                                val days = it.toIntOrNull()
                                days != null && days <= 365
                            },
                            UiText.Res(R.string.supporting_text_must_be_no_greater_than_365),
                        ),
                    ),
                onConfirm = { ancientPriceThresholdDaysString ->
                    showAncientPriceThresholdDialog = false
                    viewModel.settingsRepository.setAncientPriceThresholdDaysAsync(
                        ancientPriceThresholdDaysString.toInt()
                    )
                },
                onDismissRequest = { showAncientPriceThresholdDialog = false },
            )
        }

        if (showAnnualInflationPercentDialog) {
            SettingsDialog(
                title = stringResource(R.string.title_annual_inflation),
                subtitle = stringResource(R.string.supporting_text_annual_inflation),
                label = stringResource(R.string.title_annual_inflation),
                suffix = { Text("%") },
                initialValue = annualInflationPercent.toString(),
                validationRules =
                    listOfNotNull(
                        ValidationRule(
                            { it.trim().isNotEmpty() },
                            UiText.Res(R.string.supporting_text_required),
                        ),
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
                            UiText.Res(R.string.supporting_text_must_be_zero_or_greater),
                        ),
                        ValidationRule(
                            {
                                val inflation = it.toIntOrNull()
                                inflation != null && inflation <= 1000
                            },
                            UiText.Res(R.string.supporting_text_must_be_no_greater_than_1000),
                        ),
                    ),
                onConfirm = { annualInflationPercentString ->
                    showAnnualInflationPercentDialog = false
                    viewModel.settingsRepository.setAnnualInflationPercentAsync(
                        annualInflationPercentString.toInt()
                    )
                },
                onDismissRequest = { showAnnualInflationPercentDialog = false },
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
                icon = {
                    WarningIcon(
                        contentDescription = stringResource(R.string.content_description_warning)
                    )
                },
                title = { Text(stringResource(R.string.title_restore_from_backup)) },
                text = { Text(stringResource(R.string.message_restore_from_backup_warning)) },
                onDismissRequest = { showRestoreConfirmDialog = false },
                dismissButton = {
                    TextButton(onClick = { showRestoreConfirmDialog = false }) {
                        Text(stringResource(R.string.button_cancel))
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showRestoreConfirmDialog = false
                            onRestoreClick()
                        }
                    ) {
                        Text(stringResource(R.string.button_restore))
                    }
                },
            )
        }
    }
}

@Composable
private fun SettingsTile(title: String, subtitle: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

// Our full screen edit dialogs always have "Save" enabled but show warnings if a mandatory field is
// empty only after you've tried to save for the first time. SettingsDialog behaves differently -
// "Save" is simply disabled when there's an error or the value is empty. I think this is fine,
// because here there is a single text field so the user's attention is naturally focused on it,
// unlike a full screen dialog with multiple editable fields.
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
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentValue,
                // Put the caret at the end of the string - this is why we need a TextFieldValue.
                selection = TextRange(currentValue.length),
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    // By passing allowEmpty here, we don't validate empty strings and therefore the rule to treat
    // empty strings as invalid in the validationRules passed in to this function is irrelevant.
    // This feels like the best option here, given this is a dialog box with a single field and we
    // explicitly disable save if the field is empty. ENHANCE: We could remove that rule.
    val validatedFieldState =
        validateFieldState(
            value = currentValue,
            validationRules = validationRules,
            allowEmpty = true,
        )
    val validationResult = validatedFieldState.validationResult.value

    // I can't get any form of AnimatedVisibility or animateContentSize() to work with the
    // supportingText appearing and disappearing here, no matter where I try to put them. I don't
    // think it's a big deal. We could use an empty supportingText when there is no error to avoid
    // the dialog size changing abruptly, but I think it's probably nicer not to have the extra
    // blank space shown all the time and accept the abrupt size change.
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                Text(subtitle, modifier = Modifier.padding(bottom = 16.dp))
                NumericTextField(
                    filled = false,
                    value = textFieldValue,
                    locale = LocalConfiguration.current.locales[0],
                    onValueChange = {
                        textFieldValue = it
                        currentValue = it.text
                    },
                    label = { Text(label) },
                    suffix = suffix,
                    supportingText = textOrNull(validationResult),
                    isError = validationResult != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    interactionSource = validatedFieldState.interactionSource,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validationResult == null) {
                        onConfirm(currentValue.trim())
                    }
                },
                enabled = currentValue.trim().isNotEmpty() && validationResult == null,
            ) {
                Text(stringResource(R.string.button_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.button_cancel)) }
        },
    )

    LaunchedEffect(Unit) {
        // This delay is a ChatGPT-suggested magic value to let the dialog animation complete before
        // showing the keyboard. Apparently some versions of Android may not show the keyboard if
        // focus is requested before this point.
        delay(150)
        focusRequester.requestFocus()
    }
}
