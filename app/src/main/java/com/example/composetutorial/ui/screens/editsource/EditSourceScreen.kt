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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.composetutorial.FilteredTextField
import com.example.composetutorial.GeneralEditAndDeleteScreen
import com.example.composetutorial.LoyaltyType
import com.example.composetutorial.R
import com.example.composetutorial.createOnCandidateValueChangeMaxLength
import com.example.composetutorial.keyboardCapitalization
import com.example.composetutorial.rememberSyncedTextFieldValue
import com.example.composetutorial.ui.common.isNotBusy
import com.example.composetutorial.ui.components.SmallCircularProgressIndicator
import com.example.composetutorial.ui.components.ValidatedFilteredTextField
import com.example.composetutorial.ui.components.ValidatedNumericTextField
import com.example.composetutorial.ui.components.topAppBarTitle
import com.example.composetutorial.ui.maxNotesLength
import com.example.composetutorial.ui.maxSourceNameLength

@Composable
fun EditSourceScreen(
    viewModel: EditSourceViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

    val sourceReferenceCount by viewModel.sourceReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "sourceReferenceCount $sourceReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_source else R.string.title_delete_source_and_prices)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_source_no_associated_prices else R.string.message_delete_source_associated_prices)

    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle(if (viewModel.uiContent.editableSource.value.id == 0L) stringResource(R.string.title_add_source) else stringResource(
            R.string.title_edit_source
        ), viewModel.uiContent.dataSet.name),
        isDirty = { uiContent.editableSource.value != uiContent.originalSource },
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
        var name by rememberSyncedTextFieldValue(uiContent.editableSource.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_source_name)),
            value = name,
            maxLength = maxSourceNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableSource(uiContent.editableSource.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can I put these string versions inside LoyaltyDiscountType or won't that play well with i18n?
        val options = listOf(
            Triple(LoyaltyType.NONE, stringResource(R.string.loyalty_type_none), null),
            Triple(
                LoyaltyType.BONUS,
                stringResource(R.string.loyalty_type_bonus),
                stringResource(R.string.loyalty_type_bonus_supporting_text)
            ),
            Triple(LoyaltyType.DISCOUNT,
                stringResource(R.string.loyalty_type_discount),
                stringResource(R.string.loyalty_type_discount_supporting_text)
            )
        )
        var selectedOption = uiContent.editableSource.value.loyaltyType

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
                options.forEach { (id, name, supportingText) ->
                    val clickableModifier = if (!saveStatus.isNotBusy()) Modifier else Modifier.clickable {
                        viewModel.setUIContentEditableSource(
                            uiContent.editableSource.value.copy(
                                loyaltyType = id
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
                            enabled = saveStatus.isNotBusy(),
                            onClick = null // Row's Modifier.clickable() handles this
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

                if (selectedOption != LoyaltyType.NONE) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var loyaltyPercentage by rememberSyncedTextFieldValue(uiContent.editableSource.value.loyaltyPercentage)
                    Box(modifier = Modifier.padding(8.dp)) {
                        ValidatedNumericTextField(
                            value = loyaltyPercentage,
                            validationRules = viewModel.loyaltyPercentageValidationRules,
                            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
                            validationFlow = viewModel.saveValidationEvents,
                            validationFlowFieldId = EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                            numericTextFieldModifier = Modifier
                                .fillMaxWidth(),
                            // TODO: I can't help feeling this looks a bit confusing when it's
                            // empty, maybe it's just lack of a "%" or something.
                            label = { Text(stringResource(R.string.label_loyalty_scheme_reward)) },
                            suffix = { Text("%") },
                            onValueChange = {
                                loyaltyPercentage = it
                                viewModel.setUIContentEditableSource(
                                    uiContent.editableSource.value.copy(
                                        loyaltyPercentage = it.text
                                    )
                                )
                            },
                            enabled = saveStatus.isNotBusy(),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableSource.value.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableSource(uiContent.editableSource.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiContent.editableSource.value.id != 0L) {
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
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_delete_store))
            }
        }
    }
}
