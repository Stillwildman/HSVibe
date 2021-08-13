package com.hsvibe.ui.fragments.news

import com.google.android.material.tabs.TabLayoutMediator
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentNotificationCenterBinding
import com.hsvibe.ui.adapters.NotificationPagerAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment

/**
 * Created by Vincent on 2021/8/12.
 */
class UiNotificationFragment : BaseActionBarFragment<FragmentNotificationCenterBinding>() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_notification_center

    override fun getTitleRes(): Int = R.string.newest_info

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun onInitCompleted() {
        initTabAndPager()
    }

    private fun initTabAndPager() {
        val tabsTitle = AppController.getAppContext().resources.getStringArray(R.array.notification_tabs)

        binding.pagerNotification.adapter = NotificationPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabNotification, binding.pagerNotification) { tab, position ->
            tab.text = tabsTitle[position]
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        (binding.pagerNotification.adapter as? NotificationPagerAdapter)?.clearInstances()
    }
}