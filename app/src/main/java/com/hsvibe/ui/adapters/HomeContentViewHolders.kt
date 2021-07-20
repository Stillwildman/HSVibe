package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.databinding.InflateContentHeaderBinding
import com.hsvibe.databinding.InflateRecyclerBinding
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/7/19.
 */
class HomeContentViewHolders {

    class ContentHeaderViewHolder(val bindingView: InflateContentHeaderBinding) : RecyclerView.ViewHolder(bindingView.root)

    class ContentListViewHolder(val bindingView: InflateRecyclerBinding) : RecyclerView.ViewHolder(bindingView.root) {
        init {
            initRecycler()
        }

        private fun initRecycler() {
            bindingView.recyclerContent.apply {
                if (layoutManager == null || layoutManager?.isAttachedToWindow?.not() == false) {
                    layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
                }
            }
        }

        fun setupAdapter(adapter: BaseBindingRecycler<*>) {
            bindingView.recyclerContent.adapter = adapter
            L.i("setupAdapter!!! ${adapter::class.java.name}")
        }
    }
}