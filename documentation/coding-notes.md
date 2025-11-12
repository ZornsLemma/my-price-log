# Coding notes

## Overview

This isn't a formal coding standards document, it's just a place to put a few general notes to myself and maybe to anyone else who wants to work on the code.

## Miscellaneous notes

* Don't use raw TextFields without taking explicit steps to limit the amount of text that can be entered. In practice, use one of the custom composables which wraps a TextField and adds this kind of restriction. This limit can be fairly generous but in general we don't want users maliciously or accidentally entering megabytes of text. Even a few hundred characters in some text fields might be enough to wreck the screen layout and make the app almost unrecoverable.
