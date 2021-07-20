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
import com.hsvibe.model.UserInfoManager
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.home.UiHomeFragment
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
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
        addTabFragment(UiHomeFragment())
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

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
    }

    private fun startObserving() {
        mainViewModel.liveLoadingStatus.observe(this) { isLoading ->
            if (isLoading) {
                showLoadingCircle()
            } else {
                hideLoadingCircle()
            }
        }

        mainViewModel.liveErrorMessage.observe(this) { errorMessage ->
            Utility.toastLong(errorMessage)
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
}