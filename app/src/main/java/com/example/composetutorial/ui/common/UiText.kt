package com.example.composetutorial.ui.common

import com.example.composetutorial.ui.common.UiText
import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed class UiText {
    // TODO: I am not sure, but use of Dynamic in "final" code might be a sign something isn't
    // right. It might be that it has genuine uses in complex cases where we construct a localised
    // string via some other means, but it might be a good idea to leave it present but commented
    // out once I no longer need it.
    data class Dynamic(val text: String) : UiText()
    data class Res(@param:StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText()
    data class PluralsRes(@param:androidx.annotation.PluralsRes val resId: Int, val quantity: Int, val args: List<Any> = emptyList()) : UiText()

    fun asString(context: Context): String = when (this) {
        is Dynamic -> text
        is Res -> context.getString(resId, *args.toTypedArray())
        is PluralsRes -> context.resources.getQuantityString(resId, quantity, *args.toTypedArray())
    }

    @Composable
    fun asString(): String = asString(LocalContext.current)
}
