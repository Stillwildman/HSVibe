package com.hsvibe.model

import androidx.recyclerview.widget.DiffUtil
import com.hsvibe.model.items.ItemContent

/**
 * Created by Vincent on 2021/8/9.
 */
object DifferItems {

    object ContentItemDiffer : DiffUtil.ItemCallback<ItemContent.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemContent.ContentData, newItem: ItemContent.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: ItemContent.ContentData, newItem: ItemContent.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
                    && oldItem.title == newItem.title
                    && oldItem.content == newItem.content
                    && oldItem.share_url == newItem.share_url
        }
    }

}