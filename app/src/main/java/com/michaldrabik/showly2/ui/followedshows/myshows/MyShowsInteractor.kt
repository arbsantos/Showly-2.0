package com.michaldrabik.showly2.ui.followedshows.myshows

import com.michaldrabik.showly2.common.images.ShowImagesProvider
import com.michaldrabik.showly2.di.scope.AppScope
import com.michaldrabik.showly2.model.ImageType
import com.michaldrabik.showly2.model.MyShowsSection
import com.michaldrabik.showly2.model.MyShowsSection.ALL
import com.michaldrabik.showly2.model.MyShowsSection.COMING_SOON
import com.michaldrabik.showly2.model.MyShowsSection.ENDED
import com.michaldrabik.showly2.model.MyShowsSection.RUNNING
import com.michaldrabik.showly2.model.Show
import com.michaldrabik.showly2.model.SortOrder
import com.michaldrabik.showly2.model.SortOrder.NAME
import com.michaldrabik.showly2.model.SortOrder.NEWEST
import com.michaldrabik.showly2.model.SortOrder.RATING
import com.michaldrabik.showly2.model.mappers.Mappers
import com.michaldrabik.showly2.repository.shows.ShowsRepository
import com.michaldrabik.storage.database.AppDatabase
import javax.inject.Inject

@AppScope
class MyShowsInteractor @Inject constructor(
  private val database: AppDatabase,
  private val imagesProvider: ShowImagesProvider,
  private val mappers: Mappers,
  private val showsRepository: ShowsRepository
) {

  suspend fun loadShows(section: MyShowsSection): List<Show> {
    val sortOrder = loadSortOrder(section)
    val shows = showsRepository.myShows.loadAll()
      .filter {
        if (section.statuses.isEmpty()) true
        else section.statuses.contains(it.status)
      }
    return sortBy(sortOrder, shows)
  }

  suspend fun loadRecentShows(): List<Show> {
    val amount = loadSettings().myShowsRecentsAmount
    return showsRepository.myShows.loadAllRecent(amount)
  }

  suspend fun loadSettings() =
    mappers.settings.fromDatabase(database.settingsDao().getAll())

  private suspend fun loadSortOrder(section: MyShowsSection): SortOrder {
    val settings = loadSettings()
    return when (section) {
      RUNNING -> settings.myShowsRunningSortBy
      COMING_SOON -> settings.myShowsIncomingSortBy
      ENDED -> settings.myShowsEndedSortBy
      ALL -> settings.myShowsAllSortBy
    }
  }

  private fun sortBy(sortOrder: SortOrder, shows: List<Show>) =
    when (sortOrder) {
      NAME -> shows.sortedBy { it.title }
      NEWEST -> shows.sortedByDescending { it.year }
      RATING -> shows.sortedByDescending { it.rating }
      else -> throw IllegalStateException("Unsupported sort type.")
    }

  suspend fun setSectionSortOrder(section: MyShowsSection, order: SortOrder) {
    val settings = loadSettings()
    val newSettings = when (section) {
      RUNNING -> settings.copy(myShowsRunningSortBy = order)
      ENDED -> settings.copy(myShowsEndedSortBy = order)
      COMING_SOON -> settings.copy(myShowsIncomingSortBy = order)
      ALL -> settings.copy(myShowsAllSortBy = order)
    }
    database.settingsDao().upsert(mappers.settings.toDatabase(newSettings))
  }

  suspend fun setSectionCollapsed(section: MyShowsSection, isCollapsed: Boolean) {
    val settings = loadSettings()
    val newSettings = when (section) {
      RUNNING -> settings.copy(myShowsRunningIsCollapsed = isCollapsed)
      ENDED -> settings.copy(myShowsEndedIsCollapsed = isCollapsed)
      COMING_SOON -> settings.copy(myShowsIncomingIsCollapsed = isCollapsed)
      ALL -> settings
    }
    database.settingsDao().upsert(mappers.settings.toDatabase(newSettings))
  }

  suspend fun findCachedImage(show: Show, type: ImageType) =
    imagesProvider.findCachedImage(show, type)

  suspend fun loadMissingImage(show: Show, type: ImageType, force: Boolean) =
    imagesProvider.loadRemoteImage(show, type, force)
}
