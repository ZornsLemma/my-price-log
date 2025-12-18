package app.zornslemma.mypricelog.data

import android.content.Context
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.Quantity
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.setSelectedDataSetId
import app.zornslemma.mypricelog.ui.common.setSelectedItemId
import app.zornslemma.mypricelog.ui.common.setSelectedSourceId
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale
import kotlin.math.pow

suspend fun populateDemoData(repository: Repository, context: Context) {
    // ENHANCE: We could pick one of IMPERIAL or US_CUSTOMARY based on the current locale, but in
    // practice we just want to show we support multiple units, and it isn't as if a native US
    // customary user is going to get too confused (if they even notice) that "pint" (for example)
    // is imperial here - it's just demo data.
    // ENHANCE: We could add some demo products which are (fake) branded products rather than
    // generic categories, as this is a legitimate thing to do where the user is brand-sensitive.
    // It probably doesn't really matter though.
    val currency = Currency.getInstance(Locale.getDefault())
    // The demo data uses 2 decimal places so we scale it by currencyMultiplier when inserting so we
    // have unrealistic but at least workable prices for currencies like JPY. The prices aren't
    // meant to be realistic anyway.
    val currencyMultiplier = 10.0.pow(2 - currency.defaultFractionDigits)
    val dataSetId =
        repository.updateOrInsertDataSet(
            DataSet(
                name = context.getString(R.string.demo_groceries_data_set_name),
                currencyCode = currency.currencyCode,
                allowMetric = true,
                allowImperial = true,
                allowUSCustomary = false,
                notes = context.getString(R.string.demo_groceries_data_set_notes),
            )
        )
    val itemIdGroundCoffee =
        repository.updateOrInsertItem(
            Item(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_item_name_coffee_ground),
                defaultUnit = MeasurementUnit.G,
                allowMultipack = false,
                notes = "",
            )
        )
    val itemIdWholeMilk =
        repository.updateOrInsertItem(
            Item(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_item_name_milk_whole),
                defaultUnit = MeasurementUnit.L,
                allowMultipack = false,
                notes = "",
            )
        )
    val itemIdTeabags =
        repository.updateOrInsertItem(
            Item(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_item_name_teabags),
                defaultUnit = MeasurementUnit.EACH,
                allowMultipack = false,
                notes = "",
            )
        )
    val itemIdCola =
        repository.updateOrInsertItem(
            Item(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_item_name_cola),
                defaultUnit = MeasurementUnit.ML,
                allowMultipack = true,
                notes = "",
            )
        )
    // We have three sources with sample prices, because you need three non-ancient prices in order
    // to get good/OK/bad judgments and we want to show those off to new users.
    val sourceIdValueMart =
        repository.updateOrInsertSource(
            Source(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_source_name_valuemart),
                loyaltyType = LoyaltyType.NONE,
                loyaltyMultiplier = 1.0,
                notes = "",
            )
        )
    val sourceIdSuperiorStore =
        repository.updateOrInsertSource(
            Source(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_source_name_superiorstore),
                loyaltyType = LoyaltyType.NONE,
                loyaltyMultiplier = 1.0,
                notes = "",
            )
        )
    val sourceIdGrandways =
        repository.updateOrInsertSource(
            Source(
                dataSetId = dataSetId,
                name = context.getString(R.string.demo_groceries_source_name_grandways),
                loyaltyType = LoyaltyType.NONE,
                loyaltyMultiplier = 1.0,
                notes = "",
            )
        )
    // Newco deliberately has no prices to start with.
    repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_source_name_newco),
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = context.getString(R.string.demo_groceries_source_notes_newco),
        )
    )
    val now = Instant.now()
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdValueMart,
            price = 2.03 * currencyMultiplier,
            count = 1,
            quantity = Quantity(500.0, MeasurementUnit.G),
            confirmedAt = now.minus(2, ChronoUnit.MINUTES),
            notes = context.getString(R.string.demo_groceries_notes_large_pack_own_brand),
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(2, ChronoUnit.MINUTES),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdSuperiorStore,
            price = 1.50 * currencyMultiplier,
            count = 1,
            quantity = Quantity(227.0, MeasurementUnit.G),
            confirmedAt = now.minus(4, ChronoUnit.DAYS),
            notes = context.getString(R.string.demo_groceries_notes_own_brand),
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(4, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdGrandways,
            price = 1.64 * currencyMultiplier,
            count = 1,
            quantity = Quantity(350.0, MeasurementUnit.G),
            confirmedAt = now.minus(9, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(9, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdValueMart,
            price = 1.99 * currencyMultiplier,
            count = 1,
            quantity = Quantity(4.0, MeasurementUnit.IMPERIAL_PINT),
            confirmedAt = now,
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now,
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdSuperiorStore,
            price = 2.86 * currencyMultiplier,
            count = 1,
            quantity = Quantity(2.0, MeasurementUnit.L),
            confirmedAt = now.minus(63, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now.minus(63, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdGrandways,
            price = 3.28 * currencyMultiplier,
            count = 1,
            quantity = Quantity(6.0, MeasurementUnit.IMPERIAL_PINT),
            confirmedAt = now.minus(14, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now.minus(14, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdValueMart,
            price = 0.76 * currencyMultiplier,
            count = 1,
            quantity = Quantity(40.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(7, ChronoUnit.DAYS),
            notes = context.getString(R.string.demo_groceries_notes_soft_pack_own_brand),
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(7, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdSuperiorStore,
            price = 0.60 * currencyMultiplier,
            count = 1,
            quantity = Quantity(20.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(4, ChronoUnit.HOURS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(4, ChronoUnit.HOURS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdGrandways,
            price = 1.25 * currencyMultiplier,
            count = 1,
            quantity = Quantity(50.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(12, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(12, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdValueMart,
            price = 6.30 * currencyMultiplier,
            count = 12,
            quantity = Quantity(400.0, MeasurementUnit.ML),
            confirmedAt = now.minus(6, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(6, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdSuperiorStore,
            price = 2.79 * currencyMultiplier,
            count = 4,
            quantity = Quantity(330.0, MeasurementUnit.ML),
            confirmedAt = now.minus(31, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(31, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdGrandways,
            price = 3.82 * currencyMultiplier,
            count = 6,
            quantity = Quantity(330.0, MeasurementUnit.ML),
            confirmedAt = now.minus(18, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(18, ChronoUnit.DAYS),
        )
    )
    // Set some defaults for the first run so the user isn't left with a screen with no data
    // wondering what to do.
    setSelectedDataSetId(context, dataSetId)
    setSelectedItemId(context, dataSetId, itemIdTeabags)
    setSelectedSourceId(context, dataSetId, sourceIdSuperiorStore)
}
