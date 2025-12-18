package app.zornslemma.mypricelog.domain

import app.zornslemma.mypricelog.common.intersectionIsEmpty
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Price
import app.zornslemma.mypricelog.data.PriceHistory
import app.zornslemma.mypricelog.debug.myCheck

// Returns a version of priceList where any price measurements which are expressed in units not
// supported by the data set are changed to use a unit that is supported. This avoids some awkward
// corner cases.
//
// For example:
// - the data set allows metric and imperial
// - the user enters a price of £1.83 for 4 imperial pints of milk
// - the user changes the data set to allow only metric
//
// At this point we could:
// - Forcibly update the database to remove the no longer valid units on the price table, changing
//   our milk price to £1.83 for 2273.045ml. This would be lossy, especially if the user changed the
//   data set by accident and reverts the change later.
// - Try to ensure that all the unit-handling parts of the application take care of this specially,
//   rather than assuming that all units they encounter are currently valid according to the data
//   set's definition. In this example we might continue to show the price measurement in pints,
//   perhaps even allowing the user to edit it in pints, until they change the unit (at which point
//   they would not be able to set it back to pints, as that is not a valid unit for the data set).
//   This is mostly fine, but it feels like an invitation to subtle bugs and crashes if I forget to
//   allow for this somewhere, as well as complicating the UI code for relatively little benefit.
//   - As an unlikely but particularly awkward case, suppose we tried to accommodate the existing
//     unit for as long as possible (as just described) and the user instead had changed imperial to
//     US customary on the data set. We would be showing a price for 6 imperial pints but the
//     display would just say "pints" and the user would have no way to know the price was not in US
//     customary pints. They would perhaps even be allowed to edit the price, not realising they are
//     entering a value in imperial pints in this one case. (Note that we very deliberately do not
//     attempt to qualify ambiguous units like "pints" on the main screens for readability and
//     usability. This ambiguity is handled by not allowing both imperial and US customary for the
//     same data set. It is only the preserved unit on the price and the change of data set units
//     which re-introduce the ambiguity.)
//
// What we actually do is use this function when we read the prices out of the database, to act as
// if we forcibly updated the database to keep things consistent but without actually making those
// changes in the database itself. This avoids hidden bugs where "invalid" units can legitimately
// occur in parts of the UI code, at the minor cost of forcing the user to see the prices with
// now-invalid units in an odd but valid unit (probably ml, in our pint example). It also avoids any
// ambiguity in interpreting the units shown to the user.
fun DataSet.sanitisePriceUnits(priceList: List<Price>): List<Price> {
    val relevantUnitFamilies = getRelevantUnitFamilies()
    myCheck(relevantUnitFamilies.isNotEmpty()) {
        "Expected at least one relevant unit family for dataSet $id"
    }
    // getRelevantUnitFamilies() will in practice generate a LinkedHashSet, so first() here will be
    // deterministic and return the first family inserted. If this were to change in future, it
    // wouldn't be the end of the world, we'd just see some modest inconsistency in the results for
    // what is already a corner case.
    val replacementUnitFamily = relevantUnitFamilies.first()
    return priceList.map { price ->
        if (!intersectionIsEmpty(price.quantity.unit.unitFamilies, relevantUnitFamilies)) {
            price
        } else {
            price.copy(
                quantity =
                    price.quantity.to(
                        MeasurementUnit.entries.first {
                            replacementUnitFamily in it.unitFamilies &&
                                price.quantity.unit.quantityType == it.quantityType
                        }
                    )
            )
        }
    }
}

// This function is annoyingly similar to sanitisePriceUnits() but I don't see any way to factor out
// the commonality which isn't worse than the repetition.
fun DataSet.sanitisePriceHistoryUnits(priceHistoryList: List<PriceHistory>): List<PriceHistory> {
    val relevantUnitFamilies = getRelevantUnitFamilies()
    myCheck(relevantUnitFamilies.isNotEmpty()) {
        "Expected at least one relevant unit family for dataSet $id"
    }
    val replacementUnitFamily = relevantUnitFamilies.first() // see sanitisePriceUnits() comment
    return priceHistoryList.map { priceHistory ->
        if (!intersectionIsEmpty(priceHistory.userUnit.unitFamilies, relevantUnitFamilies)) {
            priceHistory
        } else {
            priceHistory.copy(
                userUnit =
                    MeasurementUnit.entries.first {
                        replacementUnitFamily in it.unitFamilies &&
                            priceHistory.userUnit.quantityType == it.quantityType
                    }
            )
        }
    }
}

// Similar to sanitisePriceUnits(), but for the item's default unit.
fun DataSet.sanitiseItems(itemList: List<Item>): List<Item> {
    val relevantUnitFamilies = getRelevantUnitFamilies()
    myCheck(relevantUnitFamilies.isNotEmpty()) {
        "Expected at least one relevant unit family for dataSet $id"
    }
    val replacementUnitFamily = relevantUnitFamilies.first() // see sanitisePriceUnits() comment
    return itemList.map { item ->
        if (!intersectionIsEmpty(item.defaultUnit.unitFamilies, relevantUnitFamilies)) {
            item
        } else {
            item.copy(
                defaultUnit =
                    MeasurementUnit.entries.first {
                        replacementUnitFamily in it.unitFamilies &&
                            item.defaultUnit.quantityType == it.quantityType
                    }
            )
        }
    }
}
