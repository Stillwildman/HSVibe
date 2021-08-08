package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnContentDataClickCallback
import com.hsvibe.databinding.InflateExploreBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.bases.BaseBindingPagedRecycler

/**
 * Created by Vincent on 2021/8/9.
 */
class ExploreListAdapter(private val onContentClickCallback: OnContentDataClickCallback) :
    BaseBindingPagedRecycler<ItemContent.ContentData, InflateExploreBinding>(DifferItems.ContentItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_explore

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateExploreBinding, position: Int) {
        bindingView.content = getItem(position)
        bindingView.callback = onContentClickCallback
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateExploreBinding, position: Int, payload: Any?) {

    }
}