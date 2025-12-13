package com.example.composetutorial.ui.screens.editsource

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.composetutorial.data.LoyaltyType
import com.example.composetutorial.R
import com.example.composetutorial.ui.buttonIconTextSpacing
import com.example.composetutorial.ui.components.rememberSyncedTextFieldValue
import com.example.composetutorial.ui.common.isNotBusy
import com.example.composetutorial.ui.components.FilteredTextField
import com.example.composetutorial.ui.components.SmallCircularProgressIndicator
import com.example.composetutorial.ui.components.ValidatedFilteredTextField
import com.example.composetutorial.ui.components.ValidatedNumericTextField
import com.example.composetutorial.ui.components.createOnCandidateValueChangeMaxLength
import com.example.composetutorial.ui.components.generaledit.GeneralEditAndDeleteScreen
import com.example.composetutorial.ui.components.keyboardCapitalization
import com.example.composetutorial.ui.components.topAppBarTitle
import com.example.composetutorial.ui.maxNotesLength
import com.example.composetutorial.ui.maxSourceNameLength

@Composable
fun EditSourceScreen(
    viewModel: EditSourceViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val originalSource = viewModel.uiContent.originalContent
    val editableSource by viewModel.uiContent.editableContent.collectAsStateWithLifecycle()
    val dataSet = viewModel.uiContent.staticContent.dataSet

    val sourceReferenceCount by viewModel.sourceReferenceCountFlow.collectAsStateWithLifecycle(null)

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenStateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_source else R.string.title_delete_source_and_prices)
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_source_no_associated_prices else R.string.message_delete_source_associated_prices)

    GeneralEditAndDeleteScreen(
        stateHolder = viewModel.generalEditScreenStateHolder,
        navController = navController,
        title = topAppBarTitle(if (originalSource.id == 0L) stringResource(R.string.title_add_source) else stringResource(
            R.string.title_edit_source
        ), dataSet.name),
        isDirty = { editableSource != originalSource },
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
        var name by rememberSyncedTextFieldValue(editableSource.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_source_name)),
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
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        var selectedOption = editableSource.loyaltyType

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
                    stringResource(R.string.title_loyalty_scheme),
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                LoyaltyType.entries.forEach { loyaltyType ->
                    val clickableModifier = if (!saveStatus.isNotBusy()) Modifier else Modifier.clickable {
                        viewModel.setUiContentEditableSource(
                            editableSource.copy(
                                loyaltyType = loyaltyType
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(clickableModifier)
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == loyaltyType),
                            enabled = saveStatus.isNotBusy(),
                            onClick = null // Row's Modifier.clickable() handles this
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = stringResource(loyaltyType.nameResource)
                            )
                            if (loyaltyType.supportingTextResource != null) {
                                Text(
                                    text = stringResource(loyaltyType.supportingTextResource),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (selectedOption != LoyaltyType.NONE) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var loyaltyPercentage by rememberSyncedTextFieldValue(editableSource.loyaltyPercentage)
                    Box(modifier = Modifier.padding(8.dp)) {
                        ValidatedNumericTextField(
                            value = loyaltyPercentage,
                            // TODO: We ought to be using a frozenLocale here, but right now we don't have one so doing this as a hack.
                            locale = LocalConfiguration.current.locales[0],
                            validationRules = viewModel.loyaltyPercentageValidationRules,
                            allowEmpty = !viewModel.generalEditScreenStateHolder.saveAttempted,
                            validationFlow = viewModel.saveValidationEvents,
                            validationFlowFieldId = EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                            numericTextFieldModifier = Modifier
                                .fillMaxWidth(),
                            label = { Text(stringResource(R.string.label_loyalty_scheme_reward)) },
                            suffix = { Text("%") },
                            onValueChange = {
                                loyaltyPercentage = it
                                viewModel.setUiContentEditableSource(
                                    editableSource.copy(
                                        loyaltyPercentage = it.text
                                    )
                                )
                            },
                            enabled = saveStatus.isNotBusy(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(editableSource.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
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
                Spacer(Modifier.width(buttonIconTextSpacing))
                Text(stringResource(R.string.button_delete_store))
            }
        }
    }
}
