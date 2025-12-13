// SessionManager.kt
package com.projects.a122mmtv.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object SessionManager {
    private val _logoutFlow = MutableSharedFlow<LogoutReason>(extraBufferCapacity = 1)
    val logoutFlow: SharedFlow<LogoutReason> = _logoutFlow

    fun broadcastLogout(reason: LogoutReason) {
        _logoutFlow.tryEmit(reason)
    }
}
