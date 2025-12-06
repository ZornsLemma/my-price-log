package com.example.composetutorial.ui.screens.edititem

import com.example.composetutorial.ui.components.SupportingText
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.composetutorial.R
import com.example.composetutorial.domain.areDifferentUnitFamilies
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import com.example.composetutorial.domain.getRelevantUnitFamilies
import com.example.composetutorial.ui.components.rememberSyncedTextFieldValue
import com.example.composetutorial.ui.common.isNotBusy
import com.example.composetutorial.ui.components.FilteredTextField
import com.example.composetutorial.ui.components.MyExposedDropdownMenuBox
import com.example.composetutorial.ui.components.SmallCircularProgressIndicator
import com.example.composetutorial.ui.components.ValidatedFilteredTextField
import com.example.composetutorial.ui.components.createOnCandidateValueChangeMaxLength
import com.example.composetutorial.ui.components.generaledit.GeneralEditAndDeleteScreen
import com.example.composetutorial.ui.components.keyboardCapitalization
import com.example.composetutorial.ui.components.topAppBarTitle
import com.example.composetutorial.ui.maxItemNameLength
import com.example.composetutorial.ui.maxNotesLength

@Composable
fun EditItemScreen(
    viewModel: EditItemViewModel,
    navController: NavHostController,
    requestClose: (newSelectedItemId: Long?) -> Unit
) {
    val originalItem = viewModel.uiContent.originalContent
    val editableItem by viewModel.uiContent.editableContent.collectAsStateWithLifecycle()
    val dataSet = viewModel.uiContent.staticContent.dataSet

    val itemReferenceCount by viewModel.itemReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "itemReferenceCount $itemReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenStateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_item else R.string.title_delete_item_and_prices)
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_item_no_associated_prices else R.string.message_delete_item_associated_prices)
    GeneralEditAndDeleteScreen(
        stateHolder = viewModel.generalEditScreenStateHolder,
        navController = navController,
        title = topAppBarTitle( if (viewModel.uiContent.originalContent.id == 0L) stringResource(R.string.title_add_item) else stringResource(
            R.string.title_edit_item
        ), dataSet.name),
        isDirty = { editableItem != originalItem },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave() /* ; throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            { Text(dialogTitle) },
            { Text(dialogSubtitle) },
        ),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(editableItem.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(capitalization = keyboardCapitalization(R.string.keyboard_capitalization_item_name)),
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
            validationFlowFieldId = EditItemViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Probably can/should factor out a lot of this radio button stuff which I have just
        // copied and pasted from EditSourceScreen for now.

        // ENHANCE: supportingText is always null, remove it?
        val options = listOf(
            Pair<QuantityType,String?>(
                QuantityType.ITEM,
                null
            ),
            Pair(QuantityType.WEIGHT, null),
            Pair(
                QuantityType.VOLUME,
                null,
            ),
        )
        val selectedOption = editableItem.quantityType

        // ENHANCE: When we disallow changing "sold by" because there are prices for the product,
        // just maybe we should switch to displaying a disabled TextField or similar with a
        // supportingText instead of the radio buttons. I half suspect that might look ugly and be
        // confusingly different, but maybe it wouldn't.
        val radioButtonsEnabled = saveStatus.isNotBusy() && isSimpleDelete
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            // We would like to use horizontal padding of 16.dp on this Column, but we don't want
            // the ripple effect on the radio button Rows to "stop" at the left edge of the circular
            // radio buttons. So we have to use 8.dp here and manually apply the remaining 8.dp
            // padding on each individual composable. I am not completely sure this looks great -
            // maybe it's a bit weird the ripple effect is "wider" than everything else - but it's
            // probably OK.
            Column(
                modifier = Modifier
                    // NB: We must do .animateContentSize() *before* .padding(), otherwise the clipping
                    // bounds the former imposes are too tight and will prevent ErrorHighlightBox
                    // drawing correctly.
                    .animateContentSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Text(
                    stringResource(R.string.label_sold_by),
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                // TODO: Rename "id"->quantityType?
                options.forEach { (id, supportingText) ->
                    val clickableModifier = if (!radioButtonsEnabled) Modifier else Modifier.clickable {
                        viewModel.setUiContentEditableItem(
                            editableItem.copy(
                                quantityType = id
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(Color.Blue)
                            .then(clickableModifier)
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == id),
                            enabled = radioButtonsEnabled,
                            onClick = null // the enclosing Row is clickable instead
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = stringResource(id.nameResource)
                            )
                            Log.d("MyApp", "supportingText $supportingText")
                            if (supportingText != null) {
                                Text(
                                    text = supportingText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                if (!isSimpleDelete) {
                    SupportingText(
                        stringResource(R.string.supporting_text_sold_by_cant_be_changed),
                        isError = false,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (selectedOption != QuantityType.ITEM) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // TODO: RelevantUnit* here are sort of copy and paste from ItemSourceInfo and
                    // could possibly be factored out along with the code using them
                    val relevantUnitFamilies =
                        remember(dataSet) { dataSet.getRelevantUnitFamilies() }

                    val relevantUnitList =
                        remember(
                            dataSet,
                            editableItem.quantityType
                        ) {
                            dataSet.getRelevantMeasurementUnits(
                                editableItem.quantityType,
                                includeDisplayOnly = false
                            )
                        }
                    val context = LocalContext.current
                    MyExposedDropdownMenuBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        enabled = saveStatus.isNotBusy(),
                        selectedId = editableItem.defaultUnit.id,
                        onItemSelected = {
                            val defaultUnit = MeasurementUnit.fromId(it)
                            myCheck(defaultUnit != null) {
                                "Expected non-null defaultUnit to be selected; got $it"
                            }
                            if (editableItem.defaultUnit != defaultUnit!!) {
                                val defaultUnitByQuantityType =
                                    editableItem.defaultUnitByQuantityType.toMutableMap()
                                        .also {
                                            it[editableItem.quantityType] =
                                                defaultUnit
                                        }
                                viewModel.setUiContentEditableItem(
                                    editableItem.copy(
                                        defaultUnitByQuantityType = defaultUnitByQuantityType
                                    )
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.label_default_unit)) },
                        supportingText = { Text(stringResource(R.string.supporting_text_default_unit)) },
                        items = relevantUnitList,
                        getDividerBetween = { previousItem, item -> areDifferentUnitFamilies(previousItem, item) },
                        getId = { it.id },
                        getItemText = { "${context.getString(it.fullName)} (${context.getString(it.symbol)})" },
                    )
                }
            }
        }

        // TODO END COPY-AND-PASTE-ISH RADIO BUTTON CHUNK

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.label_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    // TODO: If I change the "pack size" terminology elsewhere, need to change this too
                    text = stringResource(R.string.supporting_text_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                enabled = saveStatus.isNotBusy(),
                checked = editableItem.allowMultipack,
                onCheckedChange = {
                    viewModel.setUiContentEditableItem(
                        editableItem.copy(
                            allowMultipack = it
                        )
                    )
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(editableItem.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
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
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_delete_item))
            }
        }
    }
}
