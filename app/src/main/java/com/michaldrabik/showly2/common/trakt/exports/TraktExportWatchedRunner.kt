package com.michaldrabik.showly2.common.trakt.exports

import com.michaldrabik.network.Cloud
import com.michaldrabik.network.trakt.model.SyncExportItem
import com.michaldrabik.network.trakt.model.SyncExportRequest
import com.michaldrabik.network.trakt.model.SyncItem
import com.michaldrabik.showly2.common.trakt.TraktSyncRunner
import com.michaldrabik.showly2.di.scope.AppScope
import com.michaldrabik.showly2.repository.TraktAuthToken
import com.michaldrabik.showly2.repository.UserTraktManager
import com.michaldrabik.storage.database.AppDatabase
import com.michaldrabik.storage.database.model.Episode
import timber.log.Timber
import javax.inject.Inject

@AppScope
class TraktExportWatchedRunner @Inject constructor(
  private val cloud: Cloud,
  private val database: AppDatabase,
  userTraktManager: UserTraktManager
) : TraktSyncRunner(userTraktManager) {

  override suspend fun run(): Int {
    Timber.d("Initialized.")
    isRunning = true

    Timber.d("Checking authorization...")
    val authToken = checkAuthorization()
    exportWatched(authToken)

    isRunning = false
    Timber.d("Finished with success.")
    return 0
  }

  private suspend fun exportWatched(token: TraktAuthToken) {
    Timber.d("Exporting watched...")

    val remoteWatched = cloud.traktApi.fetchSyncWatched(token.token)
      .filter { it.show != null }
    val localMyShowsIds = database.myShowsDao().getAllTraktIds()
    val localEpisodes = database.episodesDao().getAllWatchedForShows(localMyShowsIds)
      .filter { !hasEpisodeBeenWatched(remoteWatched, it) }

    val request = SyncExportRequest(
      episodes = localEpisodes.map { SyncExportItem.create(it.idTrakt) }
    )
    cloud.traktApi.postSyncWatched(token.token, request)
  }

  private fun hasEpisodeBeenWatched(remoteWatched: List<SyncItem>, episodeDb: Episode): Boolean {
    val find = remoteWatched
      .find { it.show?.ids?.trakt == episodeDb.idShowTrakt }
      ?.seasons?.find { it.number == episodeDb.seasonNumber }
      ?.episodes?.find { it.number == episodeDb.episodeNumber }
    return find != null
  }
}
