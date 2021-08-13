package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateNotificationBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.bases.BaseBindingPagedRecycler

/**
 * Created by Vincent on 2021/8/13.
 */
class NotificationListAdapter(private val itemClickCallback: OnAnyItemClickCallback<ItemContent.ContentData>) :
    BaseBindingPagedRecycler<ItemContent.ContentData, InflateNotificationBinding>(DifferItems.ContentItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_notification

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateNotificationBinding, position: Int) {
        bindingView.content = getItem(position)
        bindingView.itemClickCallback = itemClickCallback
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateNotificationBinding, position: Int, payload: Any?) {

    }
}