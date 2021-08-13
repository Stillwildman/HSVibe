package com.hsvibe.ui.adapters

import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hsvibe.model.ApiConst
import com.hsvibe.ui.fragments.news.UiNotificationPage

/**
 * Created by Vincent on 2021/8/13.
 */
class NotificationPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val pageInstances = ArrayMap<Int, Fragment?>(2)

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return pageInstances[position] ?: run {
            if (position == 0) {
                UiNotificationPage.newInstance(ApiConst.CATEGORY_ANNOUNCEMENT)
            }
            else {
                UiNotificationPage.newInstance(ApiConst.CATEGORY_PERSONAL_NOTIFICATION)
            }
        }.also { pageInstances[position] = it }
    }

    fun clearInstances() {
        pageInstances.clear()
    }
}