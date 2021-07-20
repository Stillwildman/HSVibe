package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateCouponRowBinding
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.viewmodel.HomeViewModel

/**
 * Created by Vincent on 2021/7/20.
 */
class HomeCouponListAdapter(private val viewModel: HomeViewModel) : BaseBindingRecycler<InflateCouponRowBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_coupon_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateCouponRowBinding, position: Int) {
        bindingView.apply {
            index = position
            viewModel = this@HomeCouponListAdapter.viewModel
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder?, bindingView: InflateCouponRowBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int {
        return viewModel.getCouponDataSize()
    }
}