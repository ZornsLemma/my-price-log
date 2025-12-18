package app.zornslemma.mypricelog.ui.common

sealed class AsyncOperationStatus {
    object Idle : AsyncOperationStatus()

    object Busy : AsyncOperationStatus()

    object BusyForAWhile : AsyncOperationStatus()

    data class Success(val id: Long?) : AsyncOperationStatus()

    data class Error(val message: String) : AsyncOperationStatus()
}

fun AsyncOperationStatus.isNotBusy(): Boolean {
    return when (this) {
        is AsyncOperationStatus.Busy,
        is AsyncOperationStatus.BusyForAWhile,
        is AsyncOperationStatus.Success -> false
        else -> true
    }
}
