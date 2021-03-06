package com.michaldrabik.showly2.ui.common.base

import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.michaldrabik.showly2.ui.discover.recycler.ListItem

abstract class BaseAdapter<Item : ListItem> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AsyncListDiffer.ListListener<Item> {

  abstract val asyncDiffer: AsyncListDiffer<Item>

  var missingImageListener: (Item, Boolean) -> Unit = { _, _ -> }
  var itemClickListener: (Item) -> Unit = { }
  var listChangeListener: () -> Unit = { }
  private var notifyChange = false

  fun setItems(newItems: List<Item>, notifyChange: Boolean = false) {
    this.notifyChange = notifyChange
    asyncDiffer.removeListListener(this)
    asyncDiffer.addListListener(this)
    asyncDiffer.submitList(newItems)
  }

  override fun getItemCount() = asyncDiffer.currentList.size

  fun getItems(): List<Item> = asyncDiffer.currentList

  fun indexOf(item: Item) = asyncDiffer.currentList.indexOfFirst { it.isSameAs(item) }

  override fun onCurrentListChanged(previousList: MutableList<Item>, currentList: MutableList<Item>) {
    if (notifyChange) listChangeListener()
  }

  class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
