package app.zornslemma.mypricelog.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import app.zornslemma.mypricelog.debug.DebugFlags
import app.zornslemma.mypricelog.debug.DebugFlags.ALLOW_ROTATION_ON_PHONE
import app.zornslemma.mypricelog.ui.AppNavigation
import app.zornslemma.mypricelog.ui.theme.AppTheme

private fun Context.isPhone(): Boolean = resources.configuration.smallestScreenWidthDp < 600

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // I understand Google are discouraging apps from simply locking to portrait orientation, to
        // support tablets/ChromeOS devices better. This app is probably always going to want to run
        // in portrait on a (non-foldable) phone and I don't see any value in putting effort into
        // layouts to allow landscape to work properly on a phone. ChatGPT suggested locking to
        // portrait only on phones and I think that's a reasonable compromise for now. (There is a
        // debug override for this, since rotations are an easy way to trigger configuration changes
        // in the emulator for testing.) I haven't tested on tablets or similar devices, but I
        // suspect the app will work fine on larger screens in portrait or landscape, even if maybe
        // looks a bit odd. ENHANCE: In the future it might be nice to add alternative layouts to
        // work better on larger devices like tablets or foldables in both landscape and portrait
        // mode. This is probably not a common use case though.
        @Suppress("SimplifyBooleanWithConstants", "KotlinConstantConditions")
        if (isPhone() && !ALLOW_ROTATION_ON_PHONE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        // Target SDK >=35 directly enables edge-to-edge (see e.g.
        // https://stackoverflow.com/questions/79018063/trying-to-understand-edge-to-edge-in-android).
        // We call it here to be explicit.
        enableEdgeToEdge()

        @Suppress("KotlinConstantConditions")
        if (DebugFlags.USE_STRICT_MODE) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
        }

        setContent {
            val darkTheme = isSystemInDarkTheme()

            AppTheme(darkTheme = darkTheme) { AppNavigation() }
        }
    }
}

// ENHANCE: I have completely ignored "unlikely" errors (like exceptions being thrown when accessing
// the database) in most of this code - what can/should we do about this? I suspect most such errors
// are basically unrecoverable and it's more-or-less OK if the process just dies, but I'm not sure
// and we may be able to do better.

// The user can change the current locale while the app is running. In order to try to handle this:
// - For read-only screens, we react live to locale changes using
//   LocalConfiguration.current.locales[0], passing it as needed to non-composable code.
// - For editing screens, we "freeze" the locale from LocalConfiguration.current.locales[0] at the
//   start of editing (when locale-sensitive string representations are generated) and use this
//   locale for the duration of the editing session. This avoids issues like ambiguous
//   interpretation of "," or "." as decimal/grouping separators. (As string representations of
//   doubles may be temporarily un-parseable during editing, we cannot reliably parse them to double
//   in the old locale and re-stringify in the new locale.)
//
// Editing screens without locale-sensitive data can be treated as read-only from this perspective.
//
// In general, we avoid Locale.getDefault() and require explicit locale parameters to functions
// (without defaults) to ensure we always consider the source of our locale.
//
// ENHANCE: I came up with the above trying to "do the right thing" in what is probably a very rare
// case. I am not sure I have followed it consitstently, or that it's actually the right thing to
// do. It would probably not be that much harder to just detect locale changes and have both the
// old and new locales available, and do a "best effort" conversion of notionally numeric string
// fields the user is editing by swapping the decimal separator and grouping characters accordingly.
// I have left what I have as it is for now, because in practice it isn't that big a deal.

// ENHANCE: Maybe I should have a settings option which completely hides or just disables all the
// "delete" buttons. Users can turn that off if it makes them feel safer. We could possibly, if it
// isn't a UI nightmare, allow delete to be enabled for the next 10 minutes or something, then
// automatically re-disable. My thinking here is deletes could be very destructive of valuable data
// and in general you do not really want to delete stuff, unless you manage to add something
// completely junky rather than just adding something with a typo and needing to edit it to fix it,
// or cancelling the add before you finish it. We could also make the settings option tri-state,
// with an intermediate setting (which could perhaps even be the default) where delete buttons are
// shown/enabled (whatever I think best) for "non scary" deletes (product X is in the database *but
// no price data is attached* etc) but hidden/disabled for "scary" deletes (price data exists which
// would get cascade deleted).

// ENHANCE: M3 recommends using a "container transform pattern" to transform FAB into a full-screen
// dialog. Not sure if I can or should do this, but might be worth trying. (Do remember that as
// noted elsewhere, my "full screen dialogs" are actually full screens in their own right and I
// don't have enough hair to switch away from that, especially not just to make an animation work.
// The animation may not depend on being a "true dialog", of course.) I do wonder - not seen
// anything in docs - if this also suggests some kind of "expansion" animation should happen from
// the clicked-on source/item/dataset into the full screen dialog to edit it. Currently the code is
// doing the "standard" full screen dialog slide in from bottom animation anyway. (I had some
// discussions with LLMs about what to do for edit not add cases, where you click on a list item to
// open the edit dialog - from a UI design perspective, not how/ease of implementation. Using the
// standard slide in transform over a "container transform" was favoured 2:1 here. See how I feel
// later, and I'm far from confident I can do the FAB container transform anyway and that would
// definitely be the thing to try first (as it *is* called out in MD3 specs).)

// ENHANCE: If/when we have some kind of auto-backup or export state thing, it might be nice to hook
// this into delete operations (perhaps just cascading ones???) and auto-backup before deleting.
// Minor concern here if the user is doing a lot of deletions that we don't end up with lots of
// auto-backups, we could just possibly try to be clever and only do this if we haven't done an
// auto-backup within the last hour or so. This limits the window of data loss while keeping backup
// volume down.

// ENHANCE: Is it worth worrying about the case where the user is editing (say) an item, changes it
// name completely ("Coffee" -> "Eggs"), forgets about having done that and then hits "Delete"
// thinking they are deleting "Eggs" when they are really deleting "Coffee" (and all associated
// data)? This is probably sufficiently implausible it's not a big deal. We could find some way to
// show the "original" name on screen as a kind of reminder, but that might be confusing or clunky.
// Possibly the delete confirmation dialog could show the original name and the edited name if both
// are different, but that could be confusing if the names have just had cosmetic tweaks. Moving the
// delete operation onto the "list" screen rather than the individual item edit dialog would help
// with this, but I really don't want delete to be implemented on the list as it is a very rare and
// potentially devastating operation.

// ENHANCE: We should probably implement a "recycle bin" type delete for data set/item/source - have
// a "deleted" flag on all the tables, and when something is deleted we set that. (We would not
// cascade-set this if we delete a data set; being unable to select the data set would effectively
// hide the items/sources in it anyway, and no point forcing extra work to cacade set or unset on
// delete.) Most queries would then simply have a "deleted=false" condition to ignore deleted
// things. We can then undelete (subject to verifying names are still unique - deleted things would
// not count towards uniqueness checks, so you could create a potential duplicate after deleting
// something) simply by clearing the deleted flag. This is a UI faff because it means three-ish
// screens to select things to undelete, and maybe some other facility somewhere else to purge
// some/all things in the "recycle bin" for real. But it probably is the way to go long term.

// ENHANCE: Log.d() output is not guaranteed to be useful in a release build, particularly where
// toString() is involved. Since there is no concrete need for this to work right now, I have not
// altered it - but we may want to e.g. use Log.i() and/or toJson() logging if/when some genuine
// support case would benefit from it.

// ENHANCE: The list of prices for product across stores at bottom of home screen should probably
// have some way of expanding in place or (more likely) opening a new screen showing a read-only
// explanation of how the augmented price was arrived at (store level discounts, pseudo-inflation
// penalties, etc). This screen should probably start with the raw shelf unit price in absolute
// form, then have subsequent lines like "Inflation adjustment +$0.04" or "Loyalty discount (5%)
// -$0.03" with a final total at the end.

// Note to self: I used scaling 61% when importing app-icon-4.svg as a new image asset for the icon.

// ENHANCE: It might be nice to offer an "are you sure? this is x% more/less than before" type
// confirmation dialog when saving a price change where the (unit price? pack price? pack size?) has
// changed by more than a threshold, to help catch typos early.

// ENHANCE: I don't think it's that important, but some history editing support might be nice:
// - maybe allow the notes field to be edited in history entries ("this was a price typo")
// - maybe allow history entries to be outright deleted (expunge mistakes completely)

// ENHANCE: I sometimes start typing the name of a product into the search box at the top of the
// "edit products" screen, realise it's not there and want to add it. It might be worth (maybe gated
// by a setting) copying the search string from that screen into the name field on the add product
// screen when you click the add button in this case. This would save having to re-type it.

// ENHANCE: I perhaps ought to be more aggressive at forcing focus into text fields, e.g. when
// editing a product/source/dataset. I think there is probably an argument for *not* forcing this
// when using the "product list" screen with a search box, because the user might want to just
// scroll the list, but for the edit screens the user is going to want to edit something. It may be
// the best compromise to only do this if it is a brand new something though, as if there is already
// data, the user may not want to edit the name, which comes first and is probably what we'd force
// focus on to. And it may in practice just be best not to force it. No idea what is "standard" or
// "advised" by MD3 or general Android conventions, a chat with an LLM might offer some perspectives
// even if they're not guaranteed to be correct. It might also be a good idea to have a setting
// which controls whether we force focus onto the search control on the product selection dialog -
// some people (including me?) might nearly always want to do a text search rather than scrolling
// the list to browse, and in that case the experience is nicer if you can avoid needing to tap to
// focus.

// ENHANCE: It might be a good idea to have a setting which controls whether the price history view
// elides diffs which are nothing but confirmation date changes. And/or have a tick box on the
// screen itself to toggle this, maybe with the initial value of that tick box being set based on
// a setting. Or maybe we'd just persist the value of that tick box to a saved preference and avoid
// complicating the settings with it.

// ENHANCE: Some sort of feature for showing best ever price for a product across all stores, or
// probably better some variant on this where we show some (not too stats nerdy) "best price range"
// for data over the last n days for a product. Where I'm going with this (though there may be other
// uses) is that for products I buy rarely and on demand and am relatively price sensitive for (e.g.
// beer), I sometimes find myself reluctant to update the current price away from a temporary good
// offer price, because I probably won't be buying it tomorrow (so I don't "need" the correct price
// shown, although logically that's how the app should work/be used) and I want to record the good
// price so I know it's good when I see it again. If there was an easy way to see "best price over
// last n days" (actually showing this for say n=30/60/90/180/365 simultaneously) might not be a bad
// way to show the "spread" in a non-stats-nerd and useful way), I wouldn't feel ths reluctance to
// update the price.

// ENHANCE: A standalone unit (price) converter, although in some ways it would be nice (but not
// sure Android really has this sort of thing) if it could pop up nicely "with" other screens. But
// something where you can enter a price in one unit and have it show the unit price in any
// specified unit, a bit like a no-db version of the "Store price" card on the home screen. Maybe
// with the option to just do unit conversions (454g->lb) with no price. And maybe some sort of
// semi-persistent "printing tape" and you can press a button to "print" the current conversion onto
// it for reference, if you want to compare a few things ad-hoc without having to remember (or have
// every single thing you type "printed" and clogging up the screen). The idea being that if you're
// evaluating a different product variant to the one you already have at this store, you might want
// to explore this without relying on a unit price (if any) shown on the shelf without actually
// updating the db and finding the new price is worse. I'd envisage this being available via the
// overflow menu at top right of home screen. I'd imagine the three button metric/imperial/US
// customary selector from the dataset configuration being shown on this screen, initialised with
// the current dataset configuration, so you can choose which units appear in the dropdowns.

// ENHANCE: Add a settings option which allows toggling between explicit themes, e.g. at a minimum
// light/dark/system. It may be - really not sure - only newer Android versions with Material You
// *have* a concept of a system theme in a way that's relevant to our app, in which case we might
// want to hide or grey out the "system" option on these versions. Need to think this through at the
// time and find out what's normal and what the possibilities are.

// ENHANCE: It is possible that using SQLDelight would simplify the database queries. In particular,
// it may avoid the problem where Room flows are not clearly tagged with the query parameters that
// originated them, which I think is responsible for some of my data flow complexity in
// HomeViewModel.

// ENHANCE: In general, we set allowEmpty for our full screen dialogs based on whether a save has
// been attempted or not. The intent here is to avoid showing errors for fields which haven't been
// filled in, primarily to handle the case of adding a completely new entity where all fields start
// off blank. It might be better to set allowEmpty to "isNewEntity && !saveAttempted". This way when
// editing an existing entity, clearing out a field that can't remain empty will immediately trigger
// a validation failure, which feels consistent with how e.g. entering an invalid number will
// immediately trigger a validation failure.

// ENHANCE: It would be nice to add automated tests. At the very least, Quantity could be
// usefully tested. It would also be interesting and perhaps useful to add some unit tests for more
// of the business logic, mocking the repository, etc.

// ENHANCE: It may be a good idea to handle onQuotaExceeded() and show a notification in this case.
// This is part of the "Auto Backup for Apps" framework, which will be present on full "Google
// Android" phones and possibly some other versions of Android with ties to cloud backup. If the
// user has enabled this (system level, not part of this app as such) and the data gets too large to
// backup successfully, onQuotaExceeded() will be called and since the user may be relying on it, it
// would be good to provide a notification of some kind that their data is not being backed up even
// though they might expect it to be. Based on a quick discussion with ChatGPT there is some modest
// fiddliness in terms of showing notifications, but hooking code in here should not require
// creating any dependency on Google Play Services or similar - we can have our code called if the
// Android OS implements this feature and otherwise it will just never be called. I think we mainly
// need to implement a certain class and mention it in AndroidManifest.xml, but do check. FWIW the
// limit on Google Drive is currently 25MB per app and my personal live database is about 110K, so
// in reality it is pretty unlikely this is ever going to be a problem in the first place.
