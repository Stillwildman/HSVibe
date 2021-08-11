package com.hsvibe.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.hsvibe.ui.fragments.explore.UiExploreFragment
import com.hsvibe.ui.fragments.home.UiHomeFragment

/**
 * Created by Vincent on 2021/8/9.
 */
class TabFragmentManager(private val fm: FragmentManager, private val scope: LifecycleCoroutineScope) {

    companion object {
        const val INDEX_HOME = 0
        const val INDEX_EXPLORE = 1
        const val INDEX_COUPON = 2
        const val INDEX_WALLET = 3

        const val TAG_HOME = "TabHome"
        const val TAG_EXPLORE = "TabExplore"
        const val TAG_COUPON = "TabCoupon"
        const val TAG_WALLET = "TabWallet"
    }

    fun switchToTab(tabKey: String, containerId: Int) {
        scope.launchWhenResumed {

            fm.findFragmentByTag(tabKey)?.let { currentTab ->
                fm.beginTransaction().show(currentTab).commit()
            } ?: run {
                createTabFragment(tabKey).also { newInstance ->
                    fm.beginTransaction().add(containerId, newInstance, tabKey).commit()
                }
            }

            removeOtherTab(tabKey)
        }
    }

    private fun switchToCouponTab(couponId: String) {
        scope.launchWhenResumed {
            fm.findFragmentByTag(TAG_COUPON)?.let {
                // TODO Do customized function
                removeOtherTab(TAG_COUPON)
            } ?: run {
                // TODO New Coupon instance with params
            }
        }
    }

    private fun createTabFragment(tabKey: String): Fragment {
        return when (tabKey) {
            TAG_HOME -> UiHomeFragment()
            TAG_EXPLORE -> UiExploreFragment()
            TAG_COUPON -> UiHomeFragment() // TODO
            TAG_WALLET -> UiHomeFragment() // TODO
            else -> UiHomeFragment()
        }
    }

    private fun removeOtherTab(tabKey: String) {
        scope.launchWhenResumed {
            val ft = fm.beginTransaction()

            when (tabKey) {
                TAG_HOME -> {
                    fm.findFragmentByTag(TAG_EXPLORE)?.let { ft.remove(it) }
                    fm.findFragmentByTag(TAG_COUPON)?.let { ft.hide(it) }
                    fm.findFragmentByTag(TAG_WALLET)?.let { ft.remove(it) }
                }
                TAG_EXPLORE -> {
                    fm.findFragmentByTag(TAG_HOME)?.let { ft.hide(it) }
                    fm.findFragmentByTag(TAG_COUPON)?.let { ft.hide(it) }
                    fm.findFragmentByTag(TAG_WALLET)?.let { ft.remove(it) }
                }
                TAG_COUPON -> {
                    fm.findFragmentByTag(TAG_HOME)?.let { ft.hide(it) }
                    fm.findFragmentByTag(TAG_EXPLORE)?.let { ft.remove(it) }
                    fm.findFragmentByTag(TAG_WALLET)?.let { ft.remove(it) }
                }
                TAG_WALLET -> {
                    fm.findFragmentByTag(TAG_HOME)?.let { ft.hide(it) }
                    fm.findFragmentByTag(TAG_EXPLORE)?.let { ft.remove(it) }
                    fm.findFragmentByTag(TAG_COUPON)?.let { ft.hide(it) }
                }
            }

            ft.commit()
        }
    }
}