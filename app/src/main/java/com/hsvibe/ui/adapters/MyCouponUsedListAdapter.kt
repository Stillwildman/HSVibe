package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateCouponMineRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.ui.bases.BaseBindingDiffRecycler

/**
 * Created by Vincent on 2022/4/19.
 */
class MyCouponListAdapter(private val onCouponClick: OnAnyItemClickCallback<ItemMyCoupon.ContentData>) :
    BaseBindingDiffRecycler<ItemMyCoupon.ContentData, InflateCouponMineRowBinding>(DifferItems.MyCouponItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_coupon_mine_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponMineRowBinding, position: Int) {
        bindingView.coupon = getItem(position)
        bindingView.itemClickCallback = onCouponClick
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCouponMineRowBinding, position: Int, payload: Any?) {

    }
}