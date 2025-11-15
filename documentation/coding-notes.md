# Coding notes

TODO: Rename this? Stuff like the full screen dialog is not exactly coding, although arguably it is.

## Overview

This isn't a formal coding standards document, it's just a place to put a few general notes to myself and maybe to anyone else who wants to work on the code.

## Full screen dialogs

I must have written this out in comments or git commit messages or questions to LLMs multiple times but for the record (and writing a few days after I finally "solved" it, so my memory might be imperfect) as of right now the best way to implement this seems to be to fake it, having the full-screen dialog actually be a full screen composable accessed through the regular app navigation structure. The level of actual trickery to make this work is relatively small - really just that the enter/exit transition needs to be a dialog-like vertical slide, not a sibling-like horizontal slide. The full-screen dialog ought to have a dialog-style top bar with a close button and a "confirm" button and the back button/gesture needs overriding to behave like the close button, but those would probably be necessary however it's implemented.

The other suggestions I received from LLMs and tried very very hard to implement were:

Dialog: This is the obvious way to do it. The documentation does note that it's not intended for full-screen dialogs. The killer problem for me here was that since I needed keyboard input in my dialog, I had to allow for the on-screen keyboard sliding in and this really seemed to interact badly, even though it was near trivial to get it to work in normal full-screen composables. 

Popup: This does (I think) "guarantee" that the stuff on the popup is "on top", although it still requires finicky hacks to trap focus and avoid touch input sometimes going to the screen underneath. The killer problem for me was that a simple editable TextField didn't work on it, even using a hardware keyboard in the emulator. I never got to the point of trying it with an on-screen keyboard.

Box with high Z-order: This visually ensures our fake dialog's stuff is "on top", but (as with Popup) in ways I don't fully understand, you need to stop touch input sometimes going to the screen underneath and without the separate context (?) created by Popup, the touch input hacks become less reliable. I never actually saw a problem caused by touch input going to the lower screen, but that's not to say it could never happen. (The other miscellaneous Dialog-emulating hacks required by Popup are also required here.)

Using an actual full-screen activity which is navigated to and has a full-fledged non-dialog status avoids nearly all of this. Because it *is* a full-fledged screen, there's no "hidden" stuff which could somehow steal touch input or whatever, focus navigation of the contents "just works", the on-screen keyboard "just works" (once you make the appropriate tweaks to AndroidManifest.xml required to make this work anywhere).

## Configuration changes

Rotations are the canonical example of activities being destroyed and re-created fairly casually, but remember they are not the *only* way this happens. In particular, a light/dark theme toggle (which might happen at an arbitrary point because battery saver kicks in, for example) also does this. So although the app currently disables rotations for layout reasons, a) this might change in future b) even if it doesn't, it doesn't remove the need to handle being destroyed and re-created properly.

## Miscellaneous notes

* Don't use raw TextFields without taking explicit steps to limit the amount of text that can be entered. In practice, use one of the custom composables which wraps a TextField and adds this kind of restriction. This limit can be fairly generous but in general we don't want users maliciously or accidentally entering megabytes of text. Even a few hundred characters in some text fields might be enough to wreck the screen layout and make the app almost unrecoverable.

## Database conventions

* The primary key in a table is just called "id", not "table_id".

* Foreign keys in a table have a "_id" suffix.

* The "_id" suffix on column names is exclusively used for foreign keys. Other things (like units) which are referenced by internal code IDs don't have this suffix. (I dithered about this, but Perplexity and ChatGPT both seemed to agree on this convention so I went with it.)

* An "_at" suffix on a column name indicates a date/time represented as an Instant in EpochMilli integer form.
