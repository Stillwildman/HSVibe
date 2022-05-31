package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.InflateHomeBannerRowBinding
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.viewmodel.HomeViewModel

/**
 * Created by Vincent on 2021/7/26.
 */
class HomeBannerListAdapter(private val viewModel: HomeViewModel) : BaseBindingRecycler<InflateHomeBannerRowBinding>() {

    private val marginM by lazy { AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m) }
    private val marginL by lazy { AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_l) }

    override fun getLayoutId(): Int = R.layout.inflate_home_banner_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateHomeBannerRowBinding, position: Int) {
        bindingView.apply {
            index = position
            viewModel = this@HomeBannerListAdapter.viewModel
        }

        (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply {
            when (position) {
                0 -> {
                    marginStart = marginL
                    marginEnd = marginM
                }
                itemCount -1 -> {
                    marginStart = marginM
                    marginEnd = marginL
                }
                else -> {
                    marginStart = marginM
                    marginEnd = marginM
                }
            }
            holder.itemView.layoutParams = this
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateHomeBannerRowBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int {
        return viewModel.getBannerDataSize()
    }
}