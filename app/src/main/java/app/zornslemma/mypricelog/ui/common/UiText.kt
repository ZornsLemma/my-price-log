package app.zornslemma.mypricelog.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed class UiText {
    // "Dynamic" works perfectly well, but I suspect using it would indicate something is probably
    // not correctly translatable. I have commented it out to stop it being used by accident.
    // data class Dynamic(val text: String) : UiText()
    data class Res(@param:StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText()

    data class PluralsRes(
        @param:androidx.annotation.PluralsRes val resId: Int,
        val quantity: Int,
        val args: List<Any> = emptyList(),
    ) : UiText()

    fun asString(context: Context): String =
        when (this) {
            // is Dynamic -> text
            is Res -> context.getString(resId, *args.toTypedArray())
            is PluralsRes ->
                context.resources.getQuantityString(resId, quantity, *args.toTypedArray())
        }

    @Suppress("unused") @Composable fun asString(): String = asString(LocalContext.current)
}
