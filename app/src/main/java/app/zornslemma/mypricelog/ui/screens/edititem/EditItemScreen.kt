package app.zornslemma.mypricelog.ui.screens.edititem

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.QuantityType
import app.zornslemma.mypricelog.domain.areDifferentUnitFamilies
import app.zornslemma.mypricelog.domain.getRelevantMeasurementUnits
import app.zornslemma.mypricelog.ui.buttonIconTextSpacing
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.components.FilteredTextField
import app.zornslemma.mypricelog.ui.components.MyExposedDropdownMenuBox
import app.zornslemma.mypricelog.ui.components.RadioButtonGroup
import app.zornslemma.mypricelog.ui.components.SmallCircularProgressIndicator
import app.zornslemma.mypricelog.ui.components.SupportingText
import app.zornslemma.mypricelog.ui.components.ValidatedFilteredTextField
import app.zornslemma.mypricelog.ui.components.createOnCandidateValueChangeMaxLength
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditAndDeleteScreen
import app.zornslemma.mypricelog.ui.components.keyboardCapitalization
import app.zornslemma.mypricelog.ui.components.rememberSyncedTextFieldValue
import app.zornslemma.mypricelog.ui.components.topAppBarTitle
import app.zornslemma.mypricelog.ui.maxItemNameLength
import app.zornslemma.mypricelog.ui.maxNotesLength

@Composable
fun EditItemScreen(viewModel: EditItemViewModel, requestClose: (newSelectedItemId: Long?) -> Unit) {
    val originalItem = viewModel.uiContent.originalContent
    val editableItem by viewModel.uiContent.editableContent.collectAsStateWithLifecycle()
    val dataSet = viewModel.uiContent.staticContent.dataSet

    val itemReferenceCount by viewModel.itemReferenceCountFlow.collectAsStateWithLifecycle(null)

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by
        viewModel.generalEditScreenStateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    val dialogTitle =
        stringResource(
            if (isSimpleDelete) R.string.title_delete_item
            else R.string.title_delete_item_and_prices
        )
    val dialogSubtitle =
        stringResource(
            if (isSimpleDelete) R.string.message_delete_item_no_associated_prices
            else R.string.message_delete_item_associated_prices
        )
    GeneralEditAndDeleteScreen(
        stateHolder = viewModel.generalEditScreenStateHolder,
        title =
            topAppBarTitle(
                if (viewModel.uiContent.originalContent.id == 0L)
                    stringResource(R.string.title_add_item)
                else stringResource(R.string.title_edit_item),
                dataSet.name,
            ),
        isDirty = { editableItem != originalItem },
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
        var name by rememberSyncedTextFieldValue(editableItem.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions =
                KeyboardOptions(
                    capitalization =
                        keyboardCapitalization(R.string.keyboard_capitalization_item_name)
                ),
            value = name,
            maxLength = maxItemNameLength,
            onValueChange = {
                name = it
                viewModel.setUiContentEditableItem(editableItem.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value ?: emptyList(),
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditItemViewModel.EditableField.NAME,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val selectedOption = editableItem.quantityType

        // ENHANCE: When we disallow changing "sold by" because there are prices for the product,
        // just maybe we should switch to displaying a disabled TextField or similar with a
        // supportingText instead of the radio buttons. I half suspect that might look ugly and be
        // confusingly different, but maybe it wouldn't.
        val radioButtonsEnabled = saveStatus.isNotBusy() && isSimpleDelete
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            // We would like to use horizontal padding of 16.dp on this Column, but we don't want
            // the ripple effect on the radio button Rows to "stop" at the left edge of the circular
            // radio buttons. So we have to use 8.dp here and manually apply the remaining 8.dp
            // padding on each individual composable. I am not completely sure this looks great -
            // maybe it's a bit weird the ripple effect is "wider" than everything else - but it's
            // probably OK.
            Column(
                modifier =
                    Modifier
                        // NB: We must do .animateContentSize() *before* .padding(), otherwise the
                        // clipping bounds the former imposes are too tight and will prevent
                        // ErrorHighlightBox drawing correctly.
                        .animateContentSize()
                        .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                RadioButtonGroup(
                    title = stringResource(R.string.label_sold_by),
                    items = QuantityType.entries,
                    enabled = radioButtonsEnabled,
                    selectedId = selectedOption,
                    onItemSelected = { quantityType ->
                        viewModel.setUiContentEditableItem(
                            editableItem.copy(quantityType = quantityType)
                        )
                    },
                    getId = { it },
                    getNameResource = { it.nameResource },
                    getSupportingTextResource = { null },
                )
                if (!isSimpleDelete) {
                    SupportingText(
                        stringResource(R.string.supporting_text_sold_by_cant_be_changed),
                        isError = false,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }

                if (selectedOption != QuantityType.ITEM) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val relevantUnitList =
                        remember(dataSet, editableItem.quantityType) {
                            dataSet.getRelevantMeasurementUnits(
                                editableItem.quantityType,
                                includeDisplayOnly = false,
                            )
                        }
                    val context = LocalContext.current
                    MyExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        enabled = saveStatus.isNotBusy(),
                        selectedId = editableItem.defaultUnit.id,
                        onItemSelected = {
                            val defaultUnit = MeasurementUnit.fromId(it)
                            myCheck(defaultUnit != null) {
                                "Expected non-null defaultUnit to be selected; got $it"
                            }
                            if (editableItem.defaultUnit != defaultUnit!!) {
                                val defaultUnitByQuantityType =
                                    editableItem.defaultUnitByQuantityType.toMutableMap().also { map
                                        ->
                                        map[editableItem.quantityType] = defaultUnit
                                    }
                                viewModel.setUiContentEditableItem(
                                    editableItem.copy(
                                        defaultUnitByQuantityType = defaultUnitByQuantityType
                                    )
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.label_default_unit)) },
                        supportingText = {
                            Text(stringResource(R.string.supporting_text_default_unit))
                        },
                        items = relevantUnitList,
                        getDividerBetween = { previousItem, item ->
                            areDifferentUnitFamilies(previousItem, item)
                        },
                        getId = { it.id },
                        getItemText = {
                            "${context.getString(it.fullName)} (${context.getString(it.symbol)})"
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.label_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.supporting_text_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Switch(
                enabled = saveStatus.isNotBusy(),
                checked = editableItem.allowMultipack,
                onCheckedChange = {
                    viewModel.setUiContentEditableItem(editableItem.copy(allowMultipack = it))
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(editableItem.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions =
                KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUiContentEditableItem(editableItem.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (originalItem.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && itemReferenceCount != null,
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
                Text(stringResource(R.string.button_delete_item))
            }
        }
    }
}
