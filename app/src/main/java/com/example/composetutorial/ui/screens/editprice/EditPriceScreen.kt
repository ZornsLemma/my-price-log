package com.example.composetutorial.ui.screens.editprice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.composetutorial.FilteredTextField
import com.example.composetutorial.GeneralEditScreen
import com.example.composetutorial.R
import com.example.composetutorial.ValidatedNumericTextField
import com.example.composetutorial.areDifferentUnitFamilies
import com.example.composetutorial.createOnCandidateValueChangeMaxLength
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import com.example.composetutorial.isNotBusy
import com.example.composetutorial.keyboardCapitalization
import com.example.composetutorial.myCheck
import com.example.composetutorial.rememberSyncedTextFieldValue
import com.example.composetutorial.textOrNull
import com.example.composetutorial.topAppBarTitle
import com.example.composetutorial.ui.components.MyExposedDropdownMenuBox
import com.example.composetutorial.ui.maxNotesLength
import com.example.composetutorial.ui.nonBreakingSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPriceScreen(
    viewModel: EditPriceViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

// TODO: Some of this remember stuff should maybe move into the ViewModel

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    fun onPackSizeOrPriceChange() {
        // On the first change to the pack size or price, we set the "to confirm" switch to true, on
        // the grounds that if the user is changing these values, they must be getting them from
        // somewhere and the assumption is that they have the actual current price/pack in front of
        // them. (We don't do this if they edit the notes; it's conceivable they are for example
        // trying the product at home and making a note that a certain brand isn't very nice and not
        // to consider it as acceptable in future.) We only do this on the first change so we don't
        // fight with the user if they toggle this back off afterwards.
        // ENHANCE: We might want to gate this logic behind a Settings option, i.e. have an option to
        // let the confirm always stay off unless the user explicitly turns it on. That said, in my
        // own personal use, this logic seems to work well.
        if (!viewModel.firstPackSizeOrPriceChangeOccurred) {
            viewModel.setUIContentEditablePrice(uiContent.editablePrice.value.copy(toConfirm = true))
            viewModel.firstPackSizeOrPriceChangeOccurred = true
        }
    }

    GeneralEditScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle(viewModel.uiContent.item.name, viewModel.uiContent.source.name),
        isDirty = {
            uiContent.editablePrice.value.copy(toConfirm = false) !=
                    uiContent.originalPrice.copy(toConfirm = false)
        },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave() },
        onIdle = {},
        requestClose = requestClose,
    ) {
        // We put the price above the pack size. This matches the order we show things (at least in
        // English) on the read-only home screen. It also ties in with the price usually being the
        // primary item on a shelf label. ENHANCE: If anyone expresses an interest, we could make
        // the ordering of these translation-configurable. (Don't forget to alter the order we
        // check for validation failures to match, as well as re-ordering the actual composables
        // here.)

        EditPriceScreenPrice(viewModel, ::onPackSizeOrPriceChange)

        Spacer(modifier = Modifier.height(16.dp))

        EditPriceScreenPackSize(viewModel, ::onPackSizeOrPriceChange)

        // We don't show the switch if this is the first price for an item and source; the price is
        // confirmed, otherwise why are we entering it? Note that this is not the same as id being
        // 0, because if we deleted the price and are re-creating it from the history, we have no
        // ID but toConfirm will be false so we can preserve the old confirmation date by default.
        if (!uiContent.originalPrice.toConfirm) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.label_confirm_pack_size_and_price),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.supporting_text_details_correct_right_now),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    enabled = saveStatus.isNotBusy(),
                    checked = uiContent.editablePrice.value.toConfirm,
                    onCheckedChange = {
                        viewModel.setUIContentEditablePrice(
                            uiContent.editablePrice.value.copy(
                                toConfirm = it
                            )
                        )
                    })
            }
        } else {
            myCheck(uiContent.editablePrice.value.toConfirm) {
                "Expected toConfirm to be true as this is the first price, but it's false"
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can/should I do something to scroll the screen when focus enters this and the caret
        // is half-hidden?
        // TODO DELETE var notes by  remember { mutableStateOf(TextFieldValue(uiContent.editablePrice.value.notes)) }
        var notes by rememberSyncedTextFieldValue(uiContent.editablePrice.value.notes)
        FilteredTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditablePrice(uiContent.editablePrice.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
        )
    }
}

@Composable
private fun EditPriceScreenPrice(
    viewModel: EditPriceViewModel,
    onChange: () -> Unit
) {
    val uiContent = viewModel.uiContent

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    var packPrice by rememberSyncedTextFieldValue(uiContent.editablePrice.value.price)
    val currencyFormat = viewModel.currencyFormat

    ValidatedNumericTextField(
        value = packPrice,
        validationRules = currencyFormat.validationRules,
        // No validationRulesKey is needed as the validation rules depend only on our fixed
        // DataSet and frozen locale.
        allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
        validationFlow = viewModel.saveValidationEvents,
        validationFlowFieldId = EditPriceViewModel.EditableField.PRICE,
        errorHighlightOffset = 4.dp,
        numericTextFieldModifier = Modifier
            .fillMaxWidth(),
        // TODO: Should this really be "Shelf price" given that's what we show it as on the home screen? We should be consistent, as it *is* the same value?
        label = { Text(stringResource(R.string.label_pack_price)) },
        prefix = textOrNull(currencyFormat.prefix),
        suffix = textOrNull(currencyFormat.suffix),
        textStyle = if (currencyFormat.prefix == null && currencyFormat.suffix != null) LocalTextStyle.current.copy(
            textAlign = TextAlign.End
        ) else LocalTextStyle.current,
        onValueChange = {
            packPrice = it
            if (uiContent.editablePrice.value.price != it.text) {
                viewModel.setUIContentEditablePrice(uiContent.editablePrice.value.copy(price = it.text))
                onChange()
            }
        },
        enabled = saveStatus.isNotBusy(),
    )
}

@Composable
private fun EditPriceScreenPackSize(
    viewModel: EditPriceViewModel,
    onChange: () -> Unit
) {
    val uiContent = viewModel.uiContent

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val units: List<MeasurementUnit> =
        remember(uiContent.dataSet, uiContent.item.defaultUnit.quantityType) {
            getRelevantMeasurementUnits(
                uiContent.dataSet,
                uiContent.item.defaultUnit.quantityType,
                includeDisplayOnly = false
            )
        }
    var packCountNumber by rememberSyncedTextFieldValue(uiContent.editablePrice.value.count)
    var packSizeNumber by rememberSyncedTextFieldValue(
        uiContent.editablePrice.value.measureValue
    )

    // TODO: I wonder if this screen is actually a bit vertically (and even horizontally?) squashed
    // together, now I see that I "need" offset = 4.dp here instead of the current default 6.dp. It
    // might be I should increase the vertical spacing of the components on this screen and then
    // make this 6.dp. (I don't know, but I may have already increased the vertical spacing. So try
    // 6.dp here again - and check what other bits of the code use for their error offsets - before
    // automatically increasing the spacing.)

    // TODO: ALL THE WEIGHTS HERE INCLUDING THE LEVELS AT WHICH THEY ARE APPLIED ARE UP IN THE AIR AND SHOULD BE CHECKED

    // TODO DELETE?Column {
    if (viewModel.showPackCount) {
        //Row {//TODO(modifier = Modifier.fillMaxWidth().background(Color.Red)) {
        ValidatedNumericTextField(
            value = packCountNumber,
            validationRules = viewModel.packCountValidationRules,
            // TODO DON'T THINK WE NEED THIS BUT CHECK, WIP RIGHT NOW validationRulesKey = uiContent.editablePrice.value.measurementUnit.id,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditPriceViewModel.EditableField.PACK_COUNT,
            errorHighlightOffset = 4.dp, // TODO!?
            numericTextFieldModifier = Modifier.fillMaxWidth(), //TODObaseValidatedTextFieldModifier = Modifier.weight(1f),
            // TODO: Now this has an entire row to itself we could use a really explicit label like "Multipack count" or "Multipack quantity" if it would help - but it's probably as well not to, and don't forget ideally we are being consistent with the use of plain "count" on the supportingtext shown in the item definition for the multipack toggle
            label = { Text(stringResource(R.string.label_count)) },
            onValueChange = {
                packCountNumber = it
                if (uiContent.editablePrice.value.count != it.text) {
                    viewModel.setUIContentEditablePrice(
                        uiContent.editablePrice.value.copy(
                            count = it.text
                        )
                    )
                    onChange()
                }
            },
            enabled = saveStatus.isNotBusy(),
        )
        //}

        //TODODELETE}

        Spacer(modifier = Modifier.height(16.dp))
    }

    Row {
        ValidatedNumericTextField(
            value = packSizeNumber,
            validationRules = viewModel.packSizeValidationRules,
            validationRulesKey = uiContent.editablePrice.value.measurementUnit.id,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditPriceViewModel.EditableField.PACK_SIZE,
            errorHighlightOffset = 4.dp,
            baseValidatedTextFieldModifier = Modifier.weight(1f),
            label = { Text(stringResource(R.string.label_size)) },
            onValueChange = {
                packSizeNumber = it
                if (uiContent.editablePrice.value.measureValue != it.text) {
                    viewModel.setUIContentEditablePrice(
                        uiContent.editablePrice.value.copy(
                            measureValue = it.text
                        )
                    )
                    onChange()
                }
            },
            enabled = saveStatus.isNotBusy(),
            numericTextFieldModifier = Modifier
                // TODO DELETE? .weight(1f)
                .fillMaxSize()
        )

        if (uiContent.item.defaultUnit.quantityType != QuantityType.ITEM) {
            Spacer(modifier = Modifier.width(8.dp))

            // fontSizeDp is used here so that the minimum width we request scales
            // correctly (TODO: we hope - not tested) when the user changes the system font
            // size.
            val fontSize = MaterialTheme.typography.bodyLarge.fontSize
            val fontSizeDp = with(LocalDensity.current) { fontSize.toDp() }

            val context = LocalContext.current
            MyExposedDropdownMenuBox(
                enabled = saveStatus.isNotBusy(),
                selectedId = uiContent.editablePrice.value.measurementUnit.id,
                onItemSelected = {
                    val measurementUnit = MeasurementUnit.fromId(it)
                    myCheck(measurementUnit != null) {
                        "Expected non-null measurementUnit to be selected; got $it"
                    }
                    if (uiContent.editablePrice.value.measurementUnit != measurementUnit!!) {
                        viewModel.setUIContentEditablePrice(
                            uiContent.editablePrice.value.copy(
                                measurementUnit = measurementUnit
                            )
                        )
                        onChange()
                    }
                },
                // TODO: It might help things to have no label on this dropdown - it is right next
                // to the numeric part of the quantity and it is kind of redundant, and it might
                // help with the tight horizontal spacing. As a somewhat related problem though, the
                // horizontal space assigned to each of the composables in this Row makes no sense
                // at all and I cannot get anything I really want to work anyway, so it may be worth
                // experimenting further with this aspect too. (Outdated TODO with new layout)
                label = { Text(stringResource(R.string.label_unit)) },
                //label = { Text("") }, // TODO!?
                items = units,
                // Although this could be a problem (particularly with i18n), we give the dropdown
                // "about enough horizontal space" by calculating a hand-tuned multiplier of
                // fontSizeDp. (I cannot get it to size itself to its non-dropdown width and use
                // wrapContentWidth(), which would otherwise be ideal.) We could just give it equal
                // weight with the pack count and pack size fields and let the system size them all.
                // However, since pack count and pack size need to be able to show supportingText
                // underneath them for errors, we want to give them as much space as possible. TODO COMMENT NOW OUT OF DATE
                modifier = Modifier.weight(1f), // Modifier.width(6 * fontSizeDp), // wrapContentWidth(), // weight(0.75f), // TODO: *May* need to make this 0.5 if we don't have a count, maybe we can find something that works in both cases
                getId = { it.id },
                // It's generally a good thing that we use non-breaking spaces in unit symbols, but
                // here the symbol is used in isolation so there is no possibility of another space
                // allowing a natural break. So we turn the non-breaking space back into a regular
                // space. "fl"/"oz" is better than "fl o"/"z" if we are force to wrap.
                // TODO: With the current layout we don't really have any realistic possibility
                // of wrapping here, so we could remove this. But it isn't wrong as such.
                getCollapsedItemText = { context.getString(it.symbol).replace("$nonBreakingSpace", " ") },
                getItemText = { "${context.getString(it.fullName)} (${context.getString(it.symbol)})" },
                getDividerBetween = { previousItem, item ->
                    areDifferentUnitFamilies(
                        previousItem,
                        item
                    )
                },
            )
        }

    }
    //TODODELETE}
}
