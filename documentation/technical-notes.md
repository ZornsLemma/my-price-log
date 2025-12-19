# Technical notes

## Overview

This document just contains a few miscellaneous technical notes including some fragmentary coding standards/conventions.

## Full-screen dialogs

I must have written this out in comments or git commit messages or questions to LLMs multiple times but for the record (and writing a few days after I finally "solved" it, so my memory might be imperfect) as of right now the best way to implement this seems to be to fake it, having the full-screen dialog actually be a full-screen composable accessed through the regular app navigation structure. The level of actual trickery to make this work is relatively small - really just that the enter/exit transition needs to be a dialog-like vertical slide, not a sibling-like horizontal slide. The full-screen dialog ought to have a dialog-style top bar with a close button and a "confirm" button and the back button/gesture needs overriding to behave like the close button, but those would probably be necessary however it's implemented.

The other suggestions I received from LLMs and tried very very hard to implement were:

Dialog: This is the obvious way to do it. The documentation does note that it's not intended for full-screen dialogs. The killer problem for me here was that since I needed keyboard input in my dialog, I had to allow for the on-screen keyboard sliding in and this really seemed to interact badly, even though it was near trivial to get it to work in normal full-screen composables. 

Popup: This does (I think) "guarantee" that the stuff on the popup is "on top", although it still requires finicky hacks to trap focus and avoid touch input sometimes going to the screen underneath. The killer problem for me was that a simple editable TextField didn't work on it, even using a hardware keyboard in the emulator. I never got to the point of trying it with an on-screen keyboard.

Box with high Z-order: This visually ensures our fake dialog's stuff is "on top", but (as with Popup) in ways I don't fully understand, you need to stop touch input sometimes going to the screen underneath and without the separate context (?) created by Popup, the touch input hacks become less reliable. I never actually saw a problem caused by touch input going to the lower screen, but that's not to say it could never happen. (The other miscellaneous Dialog-emulating hacks required by Popup are also required here.)

Using an actual full-screen activity which is navigated to and has a full-fledged non-dialog status avoids nearly all of this. Because it *is* a full-fledged screen, there's no "hidden" stuff which could somehow steal touch input or whatever, focus navigation of the contents "just works", the on-screen keyboard "just works" (once you make the appropriate tweaks to AndroidManifest.xml required to make this work anywhere).

Grok suggested that I should wrap a Box with:
    Modifier.semantics {
        role = Role.Dialog // Marks this as a dialog for TalkBack
        contentDescription = "Full-screen dialog for [task, e.g., entering details]" // Optional: describe purpose
        liveRegion = LiveRegionMode.Polite // Announce when dialog opens
    }
around the Scaffold on my fake full-screen dialogs for the benefit of screen reader users, but I got conflicting advice when I attempted to follow this up so I haven't implemented anything like this yet. Even if this is a good idea, I'd rather not introduce a Box if I could just put this modifier directly on the Scaffold instead.

## Configuration changes

Rotations are the canonical example of activities being destroyed and re-created fairly casually, but remember they are not the *only* way this happens. In particular, a light/dark theme toggle (which might happen at an arbitrary point because battery saver kicks in, for example) also does this. So although the app currently disables rotations for layout reasons, a) this might change in future b) even if it doesn't, it doesn't remove the need to handle being destroyed and re-created properly.

## SavedStateHandles across app upgrades

I'm told that it's possible for this to happen:

* v1 of the app is in the background.
* Android kills it.
* The app is upgraded to v2.
* The user returns to the app.
* Android reincarnates the app, providing app v2 with SavedStateHandles from the app's previous v1 incarnation.
* The relevant serialised objects from v1 are not compatible with v2.
* Boom!

As far as I can tell this is pretty difficult to handle in the general case. If the relevant serializable objects all have default values for the new fields, things will probably work without difficulty. Otherwise we're going to have a bad time. Be aware of this when changing these objects, and if providing defaults isn't possible, it may be worth attempting workarounds like forcing an app restart (which would return us to the home screen with no old saved state) after the database upgrade.

## Constant naming and use of "const"

As a novice Kotlin/Android developer I am seeing somewhat contradictory opinions here. My inclination is to follow what I think are the widely-recognised coding standards and use UPPER_SNAKE_CASE for "const" and top-level object vals, but not to use "const" where it isn't needed and therefore be allowed to use camelCase for the name. However, if I do this, Android Studio gives me suggestions that I could make things like "val spinnerDelayMillis = 200L" const - but if I follow this suggestion, I ought to rename the variable SPINNER_DELAY_MILLIS, which I don't want to do. In order to get rid of these suggestions without introducing lots of ugly annotations or being forced to use UPPER_SNAKE_CASE, I am going to use const on them anyway.

I have used const and the probably-standard UPPER_SNAKE_CASE naming for what feel like C/C++-style build feature macros, such as LOG_SQL. This is by choice and not to get rid of the Android Studio suggestion.

TL;DR: I hope I'm doing the Right Thing except that I have added "const" in some places I otherwise wouldn't (and which thus might trigger lint warnings) just to keep Android Studio quiet.

## Miscellaneous notes

* Don't use raw TextFields without taking explicit steps to limit the amount of text that can be entered. In practice, use one of the custom composables which wraps a TextField and adds this kind of restriction. This limit can be fairly generous but in general we don't want users maliciously or accidentally entering megabytes of text. Even a few hundred characters in some text fields might be enough to wreck the screen layout and make the app almost unrecoverable.

* Locale.getDefault() is initialised to the current locale when our app process starts and is not automatically updated if the user changes the system locale while the app is running. However, Compose's LocalConfiguration.current.locales[0] is updated live and immediately reflects locale changes, triggering recompositions as needed. So we should avoid using Locale.getDefault in general, except for one-off initialisation during startup. (It is apparently possible - though risky because race conditions with recomposition and non-composable code - to update Locale.getDefault() via Locale.setDefault() to match LocalConfiguration.current.locales[0], but we avoid this.)

* `^(?!.*//).*".*"` is a reasonable regular expression to find literal strings (for localisation) which aren't in comments.

## Database conventions

* The primary key in a table is just called "id", not "table_id".

* Foreign keys in a table have a "_id" suffix.

* The "_id" suffix on column names is exclusively used for foreign keys. Other things (like units) which are referenced by internal code IDs don't have this suffix. (I dithered about this, but Perplexity and ChatGPT both seemed to agree on this convention so I went with it.)

* An "_at" suffix on a column name indicates a date/time represented as an Instant in EpochMilli integer form.

## Pre-release checklist

* Check all debug constants are set to their release values, unless there's a specific reason.

* Check for missing or out-of-date translations.

* Run spotless autoformatter.

* Bump versionCode and versionName in app/build.gradle.kts.

* Update CHANGELOG.md.
