package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateHomeNewsRowBinding
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.viewmodel.HomeViewModel

/**
 * Created by Vincent on 2021/7/20.
 */
class HomeNewsListAdapter(private val viewModel: HomeViewModel) : BaseBindingRecycler<InflateHomeNewsRowBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_home_news_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateHomeNewsRowBinding, position: Int) {
        bindingView.apply {
            index = position
            viewModel = this@HomeNewsListAdapter.viewModel
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateHomeNewsRowBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int {
        return viewModel.getContentDataSize()
    }
}