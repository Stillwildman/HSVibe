package com.hsvibe.ui.fragments.coupons

import android.os.Bundle
import com.hsvibe.R
import com.hsvibe.callbacks.OnCouponRedeemClickCallback
import com.hsvibe.databinding.FragmentCouponDetailBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.ui.bases.BaseActionBarFragment

/**
 * Created by Vincent on 2021/8/11.
 */
class UiCouponDetailFragment private constructor() : BaseActionBarFragment<FragmentCouponDetailBinding>(), OnCouponRedeemClickCallback {

    companion object {
        fun newInstance(couponItem: ItemCoupon.ContentData): UiCouponDetailFragment {
            return UiCouponDetailFragment().apply {
                arguments = Bundle(1).also { it.putParcelable(Const.BUNDLE_COUPON_DATA, couponItem) }
            }
        }
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_coupon_detail

    override fun getTitleRes(): Int? = null

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getMenuOptionIconRes(): Int = R.drawable.selector_share

    override fun onInitCompleted() {
        getBundleAndBind()
    }

    private fun getBundleAndBind() {
        val couponItem = arguments?.getParcelable<ItemCoupon.ContentData>(Const.BUNDLE_COUPON_DATA)

        couponItem?.let {
            setTitle(it.title)
            binding.coupon = it
            binding.redeemClickCallback = this
        }
    }

    override fun onMenuOptionClick() {
        // TODO Share coupon info
    }

    override fun onRedeemClick(couponItem: ItemCoupon.ContentData) {
        // TODO Redeem API?
    }
}