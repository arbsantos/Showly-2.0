package com.michaldrabik.showly2.repository.shows

import com.michaldrabik.showly2.di.scope.AppScope
import com.michaldrabik.showly2.model.IdTrakt
import com.michaldrabik.showly2.model.mappers.Mappers
import com.michaldrabik.showly2.utilities.extensions.nowUtcMillis
import com.michaldrabik.storage.database.AppDatabase
import com.michaldrabik.storage.database.model.SeeLaterShow
import javax.inject.Inject

@AppScope
class SeeLaterShowsRepository @Inject constructor(
  private val database: AppDatabase,
  private val mappers: Mappers
) {

  suspend fun loadAll() =
    database.seeLaterShowsDao().getAll()
      .map { mappers.show.fromDatabase(it) }

  suspend fun loadAllIds() = database.seeLaterShowsDao().getAllTraktIds()

  suspend fun load(id: IdTrakt) =
    database.seeLaterShowsDao().getById(id.id)?.let {
      mappers.show.fromDatabase(it)
    }

  suspend fun insert(id: IdTrakt) {
    val dbShow = SeeLaterShow.fromTraktId(id.id, nowUtcMillis())
    database.seeLaterShowsDao().insert(dbShow)
  }

  suspend fun delete(id: IdTrakt) =
    database.seeLaterShowsDao().deleteById(id.id)
}
