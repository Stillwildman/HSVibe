package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateHomeCouponRowBinding
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.viewmodel.HomeViewModel

/**
 * Created by Vincent on 2021/7/20.
 */
class HomeCouponListAdapter(private val viewModel: HomeViewModel) : BaseBindingRecycler<InflateHomeCouponRowBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_home_coupon_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateHomeCouponRowBinding, position: Int) {
        bindingView.apply {
            index = position
            viewModel = this@HomeCouponListAdapter.viewModel
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateHomeCouponRowBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int {
        return viewModel.getCouponDataSize()
    }
}