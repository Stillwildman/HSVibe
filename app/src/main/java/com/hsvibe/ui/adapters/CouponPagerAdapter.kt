package com.hsvibe.ui.adapters

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hsvibe.model.Const
import com.hsvibe.ui.fragments.coupons.UiCouponListPage

/**
 * Created by Vincent on 2021/8/13.
 */
class CouponPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val pageInstances = ArrayMap<Int, Fragment?>(2)

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return pageInstances[position] ?: run {
            if (position == 0) {
                UiCouponListPage.newInstance(Const.PAGE_MY_COUPON)
            }
            else {
                UiCouponListPage.newInstance(Const.PAGE_USED_COUPON)
            }
        }.also { pageInstances[position] = it }
    }

    fun clearInstances() {
        pageInstances.clear()
    }
}