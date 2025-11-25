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
import com.example.composetutorial.areDifferentUnitFamilies
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import com.example.composetutorial.domain.getRelevantUnitFamilies
import com.example.composetutorial.rememberSyncedTextFieldValue
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
    val uiContent = viewModel.uiContent

    val itemReferenceCount by viewModel.itemReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "itemReferenceCount $itemReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_item else R.string.title_delete_item_and_prices)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_item_no_associated_prices else R.string.message_delete_item_associated_prices)
    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle( if (viewModel.uiContent.editableItem.value.id == 0L) stringResource(R.string.title_add_item) else stringResource(
            R.string.title_edit_item
        ), viewModel.uiContent.dataSet.name),
        isDirty = { uiContent.editableItem.value != uiContent.originalItem },
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
        var name by rememberSyncedTextFieldValue(uiContent.editableItem.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(capitalization = keyboardCapitalization(R.string.keyboard_capitalization_item_name)),
            value = name,
            maxLength = maxItemNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableItem(uiContent.editableItem.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value ?: emptyList(),
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditItemViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Probably can/should factor out a lot of this radio button stuff which I have just
        // copied and pasted from EditSourceScreen for now.

        // TODO: Can I put these string versions inside QuantityType or won't that play well with i18n?
        val options = listOf(
            Triple<QuantityType,String,String?>(
                QuantityType.ITEM,
                stringResource(R.string.label_sold_by_item),
                null // was "Per item or pack of items" but probably clearer without it
            ),
            Triple(QuantityType.WEIGHT, stringResource(R.string.label_sold_by_weight), null),
            Triple(
                QuantityType.VOLUME,
                stringResource(R.string.label_sold_by_volume),
                null,
            ),
        )
        var selectedOption = uiContent.editableItem.value.quantityType

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
                options.forEach { (id, name, supportingText) ->
                    val clickableModifier = if (!radioButtonsEnabled) Modifier else Modifier.clickable {
                        viewModel.setUIContentEditableItem(
                            uiContent.editableItem.value.copy(
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
                                text = name
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
                        remember(viewModel.uiContent.dataSet) { getRelevantUnitFamilies(viewModel.uiContent.dataSet) }

                    val relevantUnitList =
                        remember(
                            viewModel.uiContent.dataSet,
                            viewModel.uiContent.editableItem.value.quantityType
                        ) {
                            getRelevantMeasurementUnits(
                                viewModel.uiContent.dataSet,
                                viewModel.uiContent.editableItem.value.quantityType,
                                includeDisplayOnly = false
                            )
                        }
                    val context = LocalContext.current
                    MyExposedDropdownMenuBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        enabled = saveStatus.isNotBusy(),
                        selectedId = uiContent.editableItem.value.defaultUnit.id,
                        onItemSelected = {
                            val defaultUnit = MeasurementUnit.fromId(it)
                            myCheck(defaultUnit != null) {
                                "Expected non-null defaultUnit to be selected; got $it"
                            }
                            if (uiContent.editableItem.value.defaultUnit != defaultUnit!!) {
                                val defaultUnitIdByQuantityTypeOrdinal =
                                    uiContent.editableItem.value.defaultUnitIdByQuantityTypeOrdinal.toMutableList()
                                        .also {
                                            it[uiContent.editableItem.value.quantityType.ordinal] =
                                                defaultUnit.id
                                        }
                                viewModel.setUIContentEditableItem(
                                    uiContent.editableItem.value.copy(
                                        defaultUnitIdByQuantityTypeOrdinal = defaultUnitIdByQuantityTypeOrdinal
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
                checked = uiContent.editableItem.value.allowMultipack,
                onCheckedChange = {
                    viewModel.setUIContentEditableItem(
                        uiContent.editableItem.value.copy(
                            allowMultipack = it
                        )
                    )
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableItem.value.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableItem(uiContent.editableItem.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiContent.editableItem.value.id != 0L) {
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
