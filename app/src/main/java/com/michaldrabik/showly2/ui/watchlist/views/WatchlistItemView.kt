package com.michaldrabik.showly2.ui.watchlist.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.michaldrabik.showly2.R
import com.michaldrabik.showly2.ui.common.views.ShowView
import com.michaldrabik.showly2.ui.watchlist.recycler.WatchlistItem
import com.michaldrabik.showly2.utilities.DurationPrinter
import com.michaldrabik.showly2.utilities.extensions.addRipple
import com.michaldrabik.showly2.utilities.extensions.bump
import com.michaldrabik.showly2.utilities.extensions.expandTouch
import com.michaldrabik.showly2.utilities.extensions.gone
import com.michaldrabik.showly2.utilities.extensions.onClick
import com.michaldrabik.showly2.utilities.extensions.visible
import com.michaldrabik.showly2.utilities.extensions.visibleIf
import kotlinx.android.synthetic.main.view_watchlist_item.view.*

@SuppressLint("SetTextI18n")
class WatchlistItemView : ShowView<WatchlistItem> {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  init {
    inflate(context, R.layout.view_watchlist_item, this)
    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    addRipple()
    watchlistItemCheckButton.expandTouch(100)
  }

  override val imageView: ImageView = watchlistItemImage
  override val placeholderView: ImageView = watchlistItemPlaceholder

  private val durationPrinter by lazy { DurationPrinter(context.applicationContext) }

  fun bind(
    item: WatchlistItem,
    itemClickListener: (WatchlistItem) -> Unit,
    detailsClickListener: (WatchlistItem) -> Unit,
    checkClickListener: (WatchlistItem) -> Unit,
    missingImageListener: (WatchlistItem, Boolean) -> Unit
  ) {
    clear()

    watchlistItemTitle.text = item.show.title
    val episodeTitle = if (item.episode.title.isBlank()) "TBA" else item.episode.title
    watchlistItemSubtitle.text = String.format(
      "S.%02d E.%02d",
      item.episode.season,
      item.episode.number
    )
    watchlistItemSubtitle2.text = episodeTitle
    watchlistItemNewBadge.visibleIf(item.isNew())

    bindProgress(item)
    bindCheckButton(item, checkClickListener, detailsClickListener)

    loadImage(item, missingImageListener)

    onClick { itemClickListener(item) }
    watchlistItemInfoButton.onClick { detailsClickListener(item) }
  }

  private fun bindProgress(item: WatchlistItem) {
    watchlistItemProgress.max = item.episodesCount
    watchlistItemProgress.progress = item.watchedEpisodesCount
    watchlistItemProgressText.text = "${item.watchedEpisodesCount}/${item.episodesCount}"
  }

  private fun bindCheckButton(
    item: WatchlistItem,
    checkClickListener: (WatchlistItem) -> Unit,
    detailsClickListener: (WatchlistItem) -> Unit
  ) {
    val hasAired = item.episode.hasAired(item.season)
    val color = if (hasAired) R.color.colorWatchlistEnabledButton else R.color.colorWatchlistDisabledButton
    if (hasAired) {
      watchlistItemCheckButton.text = ""
      watchlistItemCheckButton.setIconResource(R.drawable.ic_check)
      watchlistItemCheckButton.onClick { it.bump { checkClickListener(item) } }
      watchlistItemInfoButton.visible()
    } else {
      watchlistItemCheckButton.text = durationPrinter.print(item.episode.firstAired)
      watchlistItemCheckButton.icon = null
      watchlistItemCheckButton.onClick { detailsClickListener(item) }
      watchlistItemInfoButton.gone()
    }
    watchlistItemCheckButton.setTextColor(ContextCompat.getColor(context, color))
    watchlistItemCheckButton.setStrokeColorResource(color)
    watchlistItemCheckButton.setIconTintResource(color)
  }

  private fun clear() {
    watchlistItemTitle.text = ""
    watchlistItemSubtitle.text = ""
    watchlistItemSubtitle2.text = ""
    watchlistItemProgressText.text = ""
    watchlistItemPlaceholder.gone()
    Glide.with(this).clear(watchlistItemImage)
  }
}
