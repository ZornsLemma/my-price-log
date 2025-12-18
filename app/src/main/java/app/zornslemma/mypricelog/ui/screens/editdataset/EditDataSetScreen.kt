package app.zornslemma.mypricelog.ui.screens.editdataset

import androidx.annotation.StringRes
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
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.buttonIconTextSpacing
import app.zornslemma.mypricelog.ui.common.createCurrencyList
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.components.FilteredTextField
import app.zornslemma.mypricelog.ui.components.MyExposedDropdownMenuBox
import app.zornslemma.mypricelog.ui.components.SmallCircularProgressIndicator
import app.zornslemma.mypricelog.ui.components.SupportingText
import app.zornslemma.mypricelog.ui.components.ValidatedFilteredTextField
import app.zornslemma.mypricelog.ui.components.ValidationErrorHighlightBox
import app.zornslemma.mypricelog.ui.components.createOnCandidateValueChangeMaxLength
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditAndDeleteScreen
import app.zornslemma.mypricelog.ui.components.keyboardCapitalization
import app.zornslemma.mypricelog.ui.components.rememberSyncedTextFieldValue
import app.zornslemma.mypricelog.ui.components.textOrNull
import app.zornslemma.mypricelog.ui.components.validationInputHandleFocusRequester
import app.zornslemma.mypricelog.ui.maxDataSetNameLength
import app.zornslemma.mypricelog.ui.maxNotesLength

private enum class UnitPreferenceOption(@field:StringRes val nameResource: Int) {
    METRIC(R.string.label_metric),
    IMPERIAL(R.string.label_imperial),
    US_CUSTOMARY(R.string.label_us_units),
}

@Composable
fun EditDataSetScreen(viewModel: EditDataSetViewModel, requestClose: (Long?) -> Unit) {
    val originalDataSet = viewModel.uiContent.originalContent
    val editableDataSet by viewModel.uiContent.editableContent.collectAsStateWithLifecycle()

    val dataSetReferenceCount by
        viewModel.dataSetReferenceCountFlow.collectAsStateWithLifecycle(null)

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by
        viewModel.generalEditScreenStateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = dataSetReferenceCount == 0L
    val dialogTitle =
        stringResource(
            if (isSimpleDelete) R.string.title_delete_data_set
            else R.string.title_delete_data_set_and_associated_data
        )
    val dialogSubtitle =
        stringResource(
            if (isSimpleDelete) R.string.message_delete_data_set_no_associated_data
            else R.string.message_delete_data_set_associated_data
        )

    GeneralEditAndDeleteScreen(
        stateHolder = viewModel.generalEditScreenStateHolder,
        title = {
            Text(
                if (originalDataSet.id == 0L) stringResource(R.string.title_add_data_set)
                else stringResource(R.string.title_edit_data_set)
            )
        },
        isDirty = { editableDataSet != originalDataSet },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave() },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails =
            if (!showDeleteConfirmDialog) null
            else Triple(isSimpleDelete, { Text(dialogTitle) }, { Text(dialogSubtitle) }),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(editableDataSet.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions =
                KeyboardOptions(
                    keyboardCapitalization(R.string.keyboard_capitalization_data_set_name)
                ),
            value = name,
            maxLength = maxDataSetNameLength,
            onValueChange = {
                name = it
                viewModel.setUiContentEditableDataSet(editableDataSet.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value ?: emptyList(),
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.NAME,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ValidationErrorHighlightBox(
            value = editableDataSet.currencyCode,
            validationRules = viewModel.currencyValidationRules,
            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.CURRENCY_CODE,
        ) { validationResult, interactionSource, validationInputHandle ->
            val currentLocalConfiguration = LocalConfiguration.current
            val currencyList =
                remember(currentLocalConfiguration.locales) {
                    createCurrencyList(currentLocalConfiguration.locales)
                }

            // We try to do half-decent job by showing a gigantic list in an unwieldy dropdown but
            // putting the currencies the user is likely to care about at the top.
            //
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
                modifier =
                    Modifier.fillMaxWidth()
                        .validationInputHandleFocusRequester(validationInputHandle),
                selectedId =
                    if (editableDataSet.currencyCode != "") editableDataSet.currencyCode else null,
                onItemSelected = {
                    viewModel.setUiContentEditableDataSet(editableDataSet.copy(currencyCode = it))
                },
                enabled = saveStatus.isNotBusy(),
                label = { Text(stringResource(R.string.label_currency)) },
                items = currencyList.second,
                getId = { it.first },
                getItemText = { it.second },
                getDividerBetween = { firstItem, _ -> firstItem.first == currencyList.first },
                supportingText =
                    textOrNull(validationResult, color = MaterialTheme.colorScheme.error),
                addBottomSpace = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ENHANCE: MD3 Expressive deprecates this and says we should use a connected button group,
        // but the relevant library version is still in alpha so I'll just do it the old MD3 way for
        // now with a segmented button group.
        ValidationErrorHighlightBox(
            value = editableDataSet.unitPreferences,
            validationRules = viewModel.measurementSystemValidationRules,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.MEASUREMENT_SYSTEM,
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            Text(
                stringResource(R.string.label_measurement_units),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // We *don't* call Modifier.validationFocusRequester() as you can't focus a segmented
            // button, and this will force a clear focus to happen on validation errors instead.
            MultiChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                UnitPreferenceOption.entries.forEachIndexed { index, label ->
                    val unit = UnitPreferenceOption.entries[index]
                    val oldUnitPreferences = editableDataSet.unitPreferences
                    val checked =
                        when (unit) {
                            UnitPreferenceOption.METRIC -> oldUnitPreferences.allowMetric
                            UnitPreferenceOption.IMPERIAL -> oldUnitPreferences.allowImperial
                            UnitPreferenceOption.US_CUSTOMARY -> oldUnitPreferences.allowUSCustomary
                        }
                    SegmentedButton(
                        shape =
                            SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = UnitPreferenceOption.entries.size,
                            ),
                        onCheckedChange = {
                            // If imperial is selected, we force US customary to be deselected and
                            // vice versa. This allows us to use shorter names like "pt" instead of
                            // "pt (US)" without practical ambiguity.
                            val newUnitPreferences =
                                when (unit) {
                                    UnitPreferenceOption.METRIC ->
                                        oldUnitPreferences.copy(allowMetric = it)
                                    UnitPreferenceOption.IMPERIAL ->
                                        oldUnitPreferences.copy(
                                            allowImperial = it,
                                            allowUSCustomary =
                                                !it && oldUnitPreferences.allowUSCustomary,
                                        )
                                    UnitPreferenceOption.US_CUSTOMARY ->
                                        oldUnitPreferences.copy(
                                            allowUSCustomary = it,
                                            allowImperial = !it && oldUnitPreferences.allowImperial,
                                        )
                                }
                            viewModel.setUiContentEditableDataSet(
                                editableDataSet.copy(unitPreferences = newUnitPreferences)
                            )
                        },
                        checked = checked,
                        colors = SegmentedButtonDefaults.colors(),
                        icon = { SegmentedButtonDefaults.Icon(active = checked) },
                        enabled = true,
                    ) {
                        Text(stringResource(label.nameResource))
                    }
                }
            }

            if (validationResult != null) {
                SupportingText(
                    validationResult,
                    isError = true,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(editableDataSet.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            value = notes,
            keyboardOptions =
                KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUiContentEditableDataSet(editableDataSet.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (originalDataSet.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && dataSetReferenceCount != null,
                colors =
                    ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                // colors = ButtonDefaults.textButtonColors(contentColor =
                // MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                if (showDeleteSpinner) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.content_description_delete),
                    )
                }
                Spacer(Modifier.width(buttonIconTextSpacing))
                Text(stringResource(R.string.button_delete_collection))
            }
        }
    }
}
