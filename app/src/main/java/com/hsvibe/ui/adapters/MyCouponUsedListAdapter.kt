package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateCouponUsedRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.ui.bases.BaseBindingDiffRecycler

/**
 * Created by Vincent on 2022/4/19.
 */
class MyCouponUsedListAdapter : BaseBindingDiffRecycler<ItemMyCoupon.ContentData, InflateCouponUsedRowBinding>(DifferItems.MyCouponItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_coupon_used_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponUsedRowBinding, position: Int) {
        bindingView.item = getItem(position)
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponUsedRowBinding, position: Int, payload: Any?) {

    }
}