package com.hsvibe.ui

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.ActivityMainBinding
import com.hsvibe.location.MyFusedLocation
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.Navigation
import com.hsvibe.model.UserInfoManager
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.UiBasicWebFragment
import com.hsvibe.ui.fragments.news.UiNewsFragment
import com.hsvibe.utilities.L
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/7/4.
 */
class UiMainActivity : BaseActivity<ActivityMainBinding>(),
    TabLayout.OnTabSelectedListener,
    UserInfoManager.TokenStatusListener {

    private val mainViewModel by viewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getContainerId(): Int = R.id.fragment_container

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        L.d(TAG, "onNewIntent!!! action: ${intent?.action}")
    }

    override fun init() {
        initTabLayout()
        startObserving()
    }

    private fun initTabLayout() {
        launch {
            val tabIconResArray = arrayOf(
                R.drawable.selector_tab_home,
                R.drawable.selector_tab_explore,
                R.drawable.selector_tab_coupon,
                R.drawable.selector_tab_wallet
            )
            val tabTextResArray = arrayOf(
                R.string.tab_home,
                R.string.tab_explore,
                R.string.tab_coupon,
                R.string.tab_wallet
            )

            bindingView.mainTabLayout.apply {
                for (i in tabIconResArray.indices) {
                    val tabDeferred = async {
                        newTab().run {
                            icon = ContextCompat.getDrawable(AppController.getAppContext(), tabIconResArray[i])
                            setText(tabTextResArray[i])
                        }
                    }
                    val tab = tabDeferred.await()
                    addTab(tab, i)
                }
                addOnTabSelectedListener(this@UiMainActivity)
            }
            openFirstFragment()
        }
    }

    private fun openFirstFragment() {
        openTabFragment(TagFragmentManager.TAG_HOME)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            TagFragmentManager.INDEX_HOME -> openTabFragment(TagFragmentManager.TAG_HOME)
            TagFragmentManager.INDEX_EXPLORE -> openTabFragment(TagFragmentManager.TAG_EXPLORE)
            TagFragmentManager.INDEX_COUPON -> openTabFragment(TagFragmentManager.TAG_COUPON)
            TagFragmentManager.INDEX_WALLET -> openTabFragment(TagFragmentManager.TAG_WALLET)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onResume() {
        super.onResume()
        checkUserToken()
    }

    private fun checkUserToken() {
        UserInfoManager.checkUserTokenStatus(this)
    }

    override fun onTokenOk() {
        L.i("onTokenOk!!!")
        checkLocationSetting()

    }

    override fun onTokenExpired() {
        L.i("onTokenExpired!!!")
        mainViewModel.refreshUserToken()
    }

    override fun onTokenNull() {
        L.i("onTokenNull!!!")
        mainViewModel.setUserLoginStatus(false)
    }

    private fun startObserving() {
        mainViewModel.let {
            it.liveNavigation.observe(this) { navigation ->
                when (navigation) {
                    is Navigation.ClickingMore -> onMoreClick(navigation.apiType)
                    is Navigation.ClickingNews -> onNewsClick(navigation.itemIndex)
                    is Navigation.ClickingCoupon -> onCouponClick(navigation.couponItem)
                    is Navigation.ClickingBanner -> onBannerClick(navigation.bannerItem)
                }
            }
            it.liveLoadingStatus.observe(this) { loadingStatus ->
                handleLoadingStatus(loadingStatus)
            }
        }
    }

    private fun checkLocationSetting() {
        if (hasLocationPermission()) {
            MyFusedLocation.checkLocationSetting(this) {
                runUserInfoUpdating()
            }
        }
    }

    private fun runUserInfoUpdating() {
        mainViewModel.runUserInfoUpdating()
    }

    private fun onMoreClick(apiType: Int) {
        when (apiType) {
            ApiConst.API_TYPE_NEWS -> {
                openDialogFragment(UiNewsFragment.newInstance())
            }
            ApiConst.API_TYPE_COUPON -> {
                // TODO
            }
            ApiConst.API_TYPE_DISCOUNT -> {
                // TODO
            }
            ApiConst.API_TYPE_FOODS -> {
                // TODO
            }
            ApiConst.API_TYPE_HOTEL -> {
                // TODO
            }
        }
    }

    private fun onNewsClick(itemIndex: Int) {
        openDialogFragment(UiNewsFragment.newInstance(itemIndex))
    }

    private fun onCouponClick(couponItem: ItemCoupon.ContentData?) {

    }

    private fun onBannerClick(bannerItem: ItemBanner.ContentData?) {
        bannerItem?.let { openDialogFragment(UiBasicWebFragment.newInstance(it.share_url), Const.BACK_COMMON_DIALOG) }
    }
}