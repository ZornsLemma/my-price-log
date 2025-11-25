package com.example.composetutorial.ui.screens.editdataset

import com.example.composetutorial.ui.components.ValidationErrorHighlightBox
import com.example.composetutorial.ui.components.SupportingText
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.composetutorial.R
import com.example.composetutorial.createCurrencyList
import com.example.composetutorial.rememberSyncedTextFieldValue
import com.example.composetutorial.textOrNull
import com.example.composetutorial.ui.common.isNotBusy
import com.example.composetutorial.ui.components.FilteredTextField
import com.example.composetutorial.ui.components.MyExposedDropdownMenuBox
import com.example.composetutorial.ui.components.SmallCircularProgressIndicator
import com.example.composetutorial.ui.components.ValidatedFilteredTextField
import com.example.composetutorial.ui.components.createOnCandidateValueChangeMaxLength
import com.example.composetutorial.ui.components.generaledit.GeneralEditAndDeleteScreen
import com.example.composetutorial.ui.components.keyboardCapitalization
import com.example.composetutorial.ui.components.validationInputHandleFocusRequester
import com.example.composetutorial.ui.maxDataSetNameLength
import com.example.composetutorial.ui.maxNotesLength

private enum class UnitPreferenceOption { METRIC, IMPERIAL, US_CUSTOMARY }
// TODO: Seems quite a long function, can we factor out (even single use) chunks for readability?
@Composable
fun EditDataSetScreen(
    viewModel: EditDataSetViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

    val dataSetReferenceCount by viewModel.dataSetReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "dataSetReferenceCount $dataSetReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = dataSetReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_data_set else R.string.title_delete_data_set_and_associated_data)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_data_set_no_associated_data else R.string.message_delete_data_set_associated_data)

    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = { Text(if (uiContent.editableDataSet.value.id == 0L) stringResource(R.string.title_add_data_set) else stringResource(
            R.string.title_edit_data_set
        )) },
        isDirty = { uiContent.editableDataSet.value != uiContent.originalDataSet },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave(); /* throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        // TODO: WORDING FOR ALL OF THIS IS PARTICULARLY BAD AND NEEDS THOUGHT
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            { Text(dialogTitle) },
            { Text(dialogSubtitle) },
        ),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableDataSet.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_data_set_name)),
            value = name,
            maxLength = maxDataSetNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Should we specify an offset of 4.dp here? Or should we perhaps just improve spacing?
        ValidationErrorHighlightBox(
            value = uiContent.editableDataSet.value.currencyCode,
            validationRules = viewModel.currencyValidationRules,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.CURRENCY_CODE
        ) { validationResult, interactionSource, validationInputHandle ->
            // TODO: According to a long comment I wrote elsewhere, we probably should be using a
            // frozen LocalConfiguration from when this screen was first opened here. However, at
            // present it includes no floating point values that are awkward if the locale changes,
            // and being responsive to any locale changes is both easy and may be helpful. If I keep
            // doing it this way, I need to update that long comment elsewhere accordingly and make
            // a permanent note here too.
            val currentLocalConfiguration = LocalConfiguration.current
            val currencyList = remember(currentLocalConfiguration.locales) {
                // TODO: Test this updates if we change locales on the fly?
                createCurrencyList(currentLocalConfiguration.locales)
            }

            // We try to do half-decent job by showing a gigantic list in an unwieldy dropdown but
            // putting the currencies the user is likely to care about at the top.
            // ENHANCE: In the longer term I see three options:
            // 1 - optionally allow the user to just enter a three letter currency code directly
            // 2 - optionally allow the user to define their own currency (in which case we don't
            //     care about three letter codes) by specifying prefix, suffix and decimal places
            // 3 - investigate third party libraries to help with this
            // If option 2 is available, there may be no real need for option 1. We'd probably still
            // support currency selection in some form, but the specific escape hatch of being able
            // to type in a three letter code is not so important. But maybe we'd do both.
            //
            // We could create our own pop-up (maybe full screen?) dialog to pick a currency.
            // We could also use our existing item selection dialog - which is substring search
            // capable - to help the user pick something out of the gigantic list of currencies
            // instead of scrolling through a giant dropdown.

            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .validationInputHandleFocusRequester(validationInputHandle),

                selectedId = if (uiContent.editableDataSet.value.currencyCode != "") uiContent.editableDataSet.value.currencyCode else null,
                onItemSelected = {
                    viewModel.setUIContentEditableDataSet(
                        uiContent.editableDataSet.value.copy(
                            currencyCode = it
                        )
                    )
                },
                enabled = saveStatus.isNotBusy(),
                label = { Text(stringResource(R.string.label_currency)) },
                items = currencyList.second,
                getId = { it.first },
                getItemText = { it.second },
                getDividerBetween = { firstItem, _ -> firstItem.first == currencyList.first },
                supportingText = textOrNull(
                    validationResult,
                    color = MaterialTheme.colorScheme.error,
                ),
                addBottomSpace = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ENHANCE: MD3 Expressive deprecates this and says we should use a connected button group,
        // but the relevant library version is still in alpha so I'll just do it the old MD3 way for
        // now with a segmented button group.
        ValidationErrorHighlightBox(
            value = uiContent.editableDataSet.value.unitPreferences,
            validationRules = viewModel.measurementSystemValidationRules,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.MEASUREMENT_SYSTEM
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            Text(
                stringResource(R.string.label_measurement_units),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // "US customary" doesn't fit (on my test "small" emulated phone) but based on a discussion
            // with ChatGPT "US units" is better for a casual user anyway, even if we could fit "US
            // customary".
            // TODO: Can/should I move these names into UnitPreferenceOption? enum class
            // UnitPreferenceOption(val name: String) { METRIC("Metric"), ... }? This would make it near
            // impossible to get them out of sync and might be cleaner. I don't know if this would
            // cause i18n problems though (Grok says it's fine), so maybe leave trying this until
            // later.
            val options = listOf(stringResource(R.string.label_metric),
                stringResource(R.string.label_imperial), stringResource(R.string.label_us_units)
            ) // must match UnitPreferenceOption
            // We *don't* call Modifier.validationFocusRequester() as you can't focus a segmented
            // button, and this will force a clear focus to happen on validation errors instead.
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    val unit = UnitPreferenceOption.entries[index]
                    val oldUnitPreferences = uiContent.editableDataSet.value.unitPreferences
                    val checked = when (unit) {
                        UnitPreferenceOption.METRIC -> oldUnitPreferences.allowMetric
                        UnitPreferenceOption.IMPERIAL -> oldUnitPreferences.allowImperial
                        UnitPreferenceOption.US_CUSTOMARY -> oldUnitPreferences.allowUSCustomary
                    }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onCheckedChange = {
                            // If imperial is selected, we force US customary to be deselected and
                            // vice versa. This allows us to use shorter names like "pt" instead of
                            // "pt (US)" without practical ambiguity.
                            val newUnitPreferences = when (unit) { // TODO INLINE newUnitPreferences?
                                UnitPreferenceOption.METRIC -> oldUnitPreferences.copy(allowMetric = it)
                                UnitPreferenceOption.IMPERIAL -> oldUnitPreferences.copy(allowImperial = it, allowUSCustomary = !it && oldUnitPreferences.allowUSCustomary)
                                UnitPreferenceOption.US_CUSTOMARY -> oldUnitPreferences.copy(allowUSCustomary = it, allowImperial = !it && oldUnitPreferences.allowImperial)
                            }
                            viewModel.setUIContentEditableDataSet(
                                uiContent.editableDataSet.value.copy(unitPreferences = newUnitPreferences)
                            )
                        },
                        checked = checked,
                        colors = SegmentedButtonDefaults.colors(),
                        icon = { SegmentedButtonDefaults.Icon(active = checked) },
                        enabled = true
                    ) {
                        Text(label)
                    }
                }
            }

            if (validationResult != null) {
                SupportingText(
                    validationResult,
                    isError = true,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableDataSet.value.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            value = notes,
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiContent.editableDataSet.value.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && dataSetReferenceCount != null,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                // colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                if (showDeleteSpinner) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.content_description_delete)
                    )
                }
                Spacer(Modifier.width(8.dp)) // TODO: Maybe 16.dp given spacing around measurement units?
                Text(stringResource(R.string.button_delete_collection))
            }
        }
    }
}
