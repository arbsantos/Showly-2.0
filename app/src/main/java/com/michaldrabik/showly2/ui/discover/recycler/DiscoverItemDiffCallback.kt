package com.michaldrabik.showly2.ui.discover.recycler

import androidx.recyclerview.widget.DiffUtil

class DiscoverItemDiffCallback : DiffUtil.ItemCallback<DiscoverListItem>() {

  override fun areItemsTheSame(oldItem: DiscoverListItem, newItem: DiscoverListItem) =
    oldItem.show.id == newItem.show.id

  override fun areContentsTheSame(oldItem: DiscoverListItem, newItem: DiscoverListItem) =
    oldItem.image == newItem.image && oldItem.isLoading == newItem.isLoading
}