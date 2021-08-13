package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnContentDataClickCallback
import com.hsvibe.databinding.InflateExploreBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.bases.BaseBindingPagedRecycler
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/8/9.
 */
class ExploreListAdapter(private val onContentClickCallback: OnContentDataClickCallback) :
    BaseBindingPagedRecycler<ItemContent.ContentData, InflateExploreBinding>(DifferItems.ContentItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_explore

    private val itemHeight: Int by lazy { (Utility.getScreenHeight() * 0.65).toInt() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: InflateExploreBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false)
        return ExploreViewHolder(binding)
    }

    private inner class ExploreViewHolder(binding: InflateExploreBinding) : BindingViewHolder(binding) {
        init {
            itemView.layoutParams.height = itemHeight
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateExploreBinding, position: Int) {
        bindingView.content = getItem(position)
        bindingView.callback = onContentClickCallback
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateExploreBinding, position: Int, payload: Any?) {

    }
}