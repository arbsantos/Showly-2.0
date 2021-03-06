package com.michaldrabik.showly2.common.trakt

import com.michaldrabik.network.trakt.model.Show
import com.michaldrabik.showly2.model.error.TraktAuthError
import com.michaldrabik.showly2.repository.UserTraktManager

abstract class TraktSyncRunner(
  private val userTraktManager: UserTraktManager
) {
  var isRunning = false
  var progressListener: ((Show, Int, Int) -> Unit)? = null

  abstract suspend fun run(): Int

  protected suspend fun checkAuthorization() = try {
    userTraktManager.checkAuthorization()
  } catch (t: Throwable) {
    isRunning = false
    throw TraktAuthError(t.message)
  }
}
