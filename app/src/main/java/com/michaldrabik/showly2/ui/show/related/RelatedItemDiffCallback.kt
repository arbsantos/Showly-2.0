package com.michaldrabik.showly2.ui.show.related

import androidx.recyclerview.widget.DiffUtil

class RelatedItemDiffCallback : DiffUtil.ItemCallback<RelatedListItem>() {

  override fun areItemsTheSame(oldItem: RelatedListItem, newItem: RelatedListItem) =
    oldItem.show.id == newItem.show.id

  override fun areContentsTheSame(oldItem: RelatedListItem, newItem: RelatedListItem) =
    oldItem.image == newItem.image && oldItem.isLoading == newItem.isLoading
}