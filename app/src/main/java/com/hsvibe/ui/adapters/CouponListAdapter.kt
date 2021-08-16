package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateCouponRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.ui.bases.BaseBindingPagedRecycler

/**
 * Created by Vincent on 2021/8/16.
 */
class CouponListAdapter(
    private val onCouponClick: OnAnyItemClickCallback<ItemCoupon.ContentData>,
    private val onShareClick: OnAnyItemClickCallback<ItemCoupon.ContentData>
) : BaseBindingPagedRecycler<ItemCoupon.ContentData, InflateCouponRowBinding>(DifferItems.CouponItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_coupon_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponRowBinding, position: Int) {
        bindingView.apply {
            coupon = getItem(position)
            itemClickCallback = onCouponClick
            shareClickCallback = onShareClick
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponRowBinding, position: Int, payload: Any?) {

    }
}