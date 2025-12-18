package app.zornslemma.mypricelog.ui.screens.editsource

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.LoyaltyType
import app.zornslemma.mypricelog.ui.buttonIconTextSpacing
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.components.FilteredTextField
import app.zornslemma.mypricelog.ui.components.RadioButtonGroup
import app.zornslemma.mypricelog.ui.components.SmallCircularProgressIndicator
import app.zornslemma.mypricelog.ui.components.ValidatedFilteredTextField
import app.zornslemma.mypricelog.ui.components.ValidatedNumericTextField
import app.zornslemma.mypricelog.ui.components.createOnCandidateValueChangeMaxLength
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditAndDeleteScreen
import app.zornslemma.mypricelog.ui.components.keyboardCapitalization
import app.zornslemma.mypricelog.ui.components.rememberSyncedTextFieldValue
import app.zornslemma.mypricelog.ui.components.topAppBarTitle
import app.zornslemma.mypricelog.ui.maxNotesLength
import app.zornslemma.mypricelog.ui.maxSourceNameLength

@Composable
fun EditSourceScreen(viewModel: EditSourceViewModel, requestClose: (Long?) -> Unit) {
    val originalSource = viewModel.uiContent.originalContent
    val editableSource by viewModel.uiContent.editableContent.collectAsStateWithLifecycle()
    val dataSet = viewModel.uiContent.staticContent.dataSet

    val sourceReferenceCount by viewModel.sourceReferenceCountFlow.collectAsStateWithLifecycle(null)

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by
        viewModel.generalEditScreenStateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    val dialogTitle =
        stringResource(
            if (isSimpleDelete) R.string.title_delete_source
            else R.string.title_delete_source_and_prices
        )
    val dialogSubtitle =
        stringResource(
            if (isSimpleDelete) R.string.message_delete_source_no_associated_prices
            else R.string.message_delete_source_associated_prices
        )

    GeneralEditAndDeleteScreen(
        stateHolder = viewModel.generalEditScreenStateHolder,
        title =
            topAppBarTitle(
                if (originalSource.id == 0L) stringResource(R.string.title_add_source)
                else stringResource(R.string.title_edit_source),
                dataSet.name,
            ),
        isDirty = { editableSource != originalSource },
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
        var name by rememberSyncedTextFieldValue(editableSource.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions =
                KeyboardOptions(
                    keyboardCapitalization(R.string.keyboard_capitalization_source_name)
                ),
            value = name,
            maxLength = maxSourceNameLength,
            onValueChange = {
                name = it
                viewModel.setUiContentEditableSource(editableSource.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value ?: emptyList(),
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val selectedOption = editableSource.loyaltyType

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
                    title = stringResource(R.string.title_loyalty_scheme),
                    items = LoyaltyType.entries,
                    enabled = saveStatus.isNotBusy(),
                    selectedId = selectedOption,
                    onItemSelected = { loyaltyType ->
                        viewModel.setUiContentEditableSource(
                            editableSource.copy(loyaltyType = loyaltyType)
                        )
                    },
                    getId = { it },
                    getNameResource = { it.nameResource },
                    getSupportingTextResource = { it.supportingTextResource },
                )

                if (selectedOption != LoyaltyType.NONE) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var loyaltyPercentage by
                        rememberSyncedTextFieldValue(editableSource.loyaltyPercentage)
                    Box(modifier = Modifier.padding(8.dp)) {
                        ValidatedNumericTextField(
                            value = loyaltyPercentage,
                            locale = viewModel.uiContent.staticContent.frozenLocale,
                            validationRules = viewModel.loyaltyPercentageValidationRules,
                            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
                            validationFlow = viewModel.saveValidationEvents,
                            validationFlowFieldId =
                                EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                            numericTextFieldModifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.label_loyalty_scheme_reward)) },
                            suffix = { Text("%") },
                            onValueChange = {
                                loyaltyPercentage = it
                                viewModel.setUiContentEditableSource(
                                    editableSource.copy(loyaltyPercentage = it.text)
                                )
                            },
                            enabled = saveStatus.isNotBusy(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(editableSource.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions =
                KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUiContentEditableSource(editableSource.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (originalSource.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && sourceReferenceCount != null,
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
                Text(stringResource(R.string.button_delete_store))
            }
        }
    }
}
