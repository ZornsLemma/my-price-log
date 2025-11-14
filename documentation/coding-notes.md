# Coding notes

## Overview

This isn't a formal coding standards document, it's just a place to put a few general notes to myself and maybe to anyone else who wants to work on the code.

## Miscellaneous notes

* Don't use raw TextFields without taking explicit steps to limit the amount of text that can be entered. In practice, use one of the custom composables which wraps a TextField and adds this kind of restriction. This limit can be fairly generous but in general we don't want users maliciously or accidentally entering megabytes of text. Even a few hundred characters in some text fields might be enough to wreck the screen layout and make the app almost unrecoverable.

## Database conventions

* The primary key in a table is just called "id", not "table_id".

* Foreign keys in a table have a "_id" suffix.

* The "_id" suffix on column names is exclusively used for foreign keys. Other things (like units) which are referenced by internal code IDs don't have this suffix. (I dithered about this, but Perplexity and ChatGPT both seemed to agree on this convention so I went with it.)

* An "_at" suffix on a column name indicates a date/time represented as an Instant in EpochMilli integer form.
