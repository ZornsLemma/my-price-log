package app.zornslemma.mypricelog.ui.components

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.annotation.AnyRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization

private const val TAG = "KeyboardCapitalization"

@Composable
fun keyboardCapitalization(@StringRes resId: Int): KeyboardCapitalization =
    when (val option = stringResource(resId)) {
        "characters" -> KeyboardCapitalization.Characters
        "none" -> KeyboardCapitalization.None
        "sentences" -> KeyboardCapitalization.Sentences
        "words" -> KeyboardCapitalization.Words
        else -> {
            Log.w(
                TAG,
                "Resource '${resourceName(LocalContext.current, resId)}' has unknown keyboard capitalization option '$option'",
            )
            KeyboardCapitalization.None
        }
    }

private fun resourceName(context: Context, @AnyRes resId: Int): String =
    try {
        context.resources.getResourceEntryName(resId)
    } catch (e: Resources.NotFoundException) {
        "unknown resource $resId"
    }
