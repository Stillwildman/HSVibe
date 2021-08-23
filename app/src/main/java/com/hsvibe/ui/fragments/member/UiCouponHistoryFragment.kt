package com.hsvibe.ui.fragments.member

import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentCouponHistoryBinding
import com.hsvibe.ui.adapters.CouponPagerAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment

/**
 * Created by Vincent on 2021/8/23.
 */
class UiCouponHistoryFragment : BaseActionBarFragment<FragmentCouponHistoryBinding>() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_coupon_history

    override fun getTitleRes(): Int = R.string.my_coupons

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    override fun onInitCompleted() {
        initTabAndPager()
    }

    private fun initTabAndPager() {
        val tabsTitle = AppController.getAppContext().resources.getStringArray(R.array.coupon_history_tabs)

        binding.pagerCouponHistory.adapter = CouponPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabCouponHistory, binding.pagerCouponHistory) { tab, position ->
            tab.text = tabsTitle[position]
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        (binding.pagerCouponHistory.adapter as? CouponPagerAdapter)?.clearInstances()
    }
}