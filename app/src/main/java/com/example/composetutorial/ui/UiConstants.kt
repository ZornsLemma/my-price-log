package com.example.composetutorial.ui

import androidx.compose.ui.unit.dp

// TODO: Should these be using UPPER_SNAKE_CASE names!?

// Unicode characters expressed explicitly via variables to make it obvious where they are used
// (rather than relying on us recognising visually that we have "×" rather than "x" in a string
// literal).
const val multiplicationSign = "\u00d7"
const val emDash = "\u2014"
const val copyrightSymbol = "\u00a9"
const val bulletPoint = "\u2022"
const val nonBreakingSpace = "\u00a0"

// Since all our data is local, we generally expect to be able to respond promptly to user requests.
// Things like the dropdown they touched closing or the button they touched animating provide
// feedback that their touch has been noticed. We don't immediately show a spinner because AIUI
// "short" delays are mostly perceived as instantaneous, and if we showed a spinner (especially a
// full screen one with scrim) immediately only to remove it after 50ms, that would be jarring.
// Instead we leave the UI unaltered for spinnerDelay ms; if we complete our operations within that
// time, the user never sees a spinner. If things take longer than that, we need to do something as
// the user is not going to perceive the operation as instantaneous anyway, so we show a spinner
// until it completes. We don't update the UI until the operation completes - we acknowledge a
// user's change to a dropdown by the dropdown disappearing, but we don't want to show the new value
// in that dropdown immediately while the rest of the screen still contains data related to the old
// value. The spinner (which in this case is likely to be on a full-screen scrim) shows that the
// on-screen data is outdated, but we retain consistency. Even if the data retrieval is quicker than
// spinnerDelay ms, we don't want a janky double-update where the dropdown's content changes
// instantly then the associated data changes a few ms later.
const val spinnerDelayMillis = 200L

// This value is a trade-off between showing the user validation failures ASAP and not annoying them
// by showing transient validation failures while they are in the middle of actively editing. This
// feels reasonable-ish and we can always tweak it later.
// TODO: a whole second feels insanely slow
const val defaultValidationMessageDelayMillis = 200L

// TODO: If this is too long, the user can break something different, click Save again and have to
// wait until the first animation finishes. Let's start with 1000 and see how it goes.
const val errorHighlightBoxVisibleTimeMillis = 1000L

// TODO: https://m3.material.io/foundations/layout/applying-layout/compact says 16dp left and right
// margins - maybe change this? Then again there are places where I've used edge-to-edge for lists
// so I just don't know. For that matter, are there 16.dp margins supposed to be added to left and
// right of the top app bar as well? Or just the body content below it?
val screenBorder = 8.dp

// MD3 specs say there should be a 24.dp horizontal border, but this seems quite ugly. The left hand
// edge of the dialog's body controls don't line up with the close icon and the right hand edges
// don't line up with the right hand edge of the "Save" text button. Some of the screenshots in the
// documentation seem to show some but not all of these misalignments. It just feels half-baked and
// inconsistent so I'm going to go with this.
val fullScreenDialogHorizontalBorder = 16.dp

val fullScreenDialogVerticalBorder = 8.dp

// MD3 says 12.dp but MyExposedDropdownMenuBox's dropdown item text doesn't line up with the parent
// TextField text with that.
val menuLeftPadding = 16.dp
// Seems best to make the right padding symmetrical.
val menuRightPadding = menuLeftPadding

val defaultErrorHighlightOffset = 6.dp

// MD3 (while deprecating the navigation drawer anyway) says the width should be 360.dp. We don't
// properly respect that because I think it looks bad on a phone to have the drawer fill the whole
// screen, but we do respect it as far as using it as a maximum width. This will probably never kick
// in unless someone is using the app on a tablet, but still.
val maxNavigationDrawerWidth = 360.dp

// MD3 standard values
val oneLineListItemHeight = 56.dp
val listItemHorizontalPadding = 16.dp

// These arbitrary lengths apply to the UI only (not the database) and are just intended to stop the
// user typing insane amounts of text into TextFields and breaking layouts. They may need to be
// tweaked later.
const val maxDataSetNameLength = 32
const val maxItemNameLength = 32
const val maxSourceNameLength = 32
const val maxNotesLength = 1024
const val maxSearchLength = 32

// 11 is a bit arbitrary but we're just trying to avoid the user filling the TextField with hundreds
// of characters of junk and breaking the screen layout badly. 11 is pretty generous as it allows
// just under a million with two decimal places and a (manually entered) thousands separator, so we
// could tighten this up a bit if desirable. TODO: Might be worth tightening this up, partly in view of new layouts, and partly given it will be easier to relax this post-release than to tighten it
const val maxDecimalLength = 11
