package com.hsvibe.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.ActivityMainBinding
import com.hsvibe.location.MyFusedLocation
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.Navigation
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.coupons.UiCouponDetailFragment
import com.hsvibe.ui.fragments.login.UiLoginWebDialogFragment
import com.hsvibe.ui.fragments.member.UiMemberCenterFragment
import com.hsvibe.ui.fragments.member.UiMemberInfoFragment
import com.hsvibe.ui.fragments.news.UiNewsFragment
import com.hsvibe.ui.fragments.news.UiNotificationFragment
import com.hsvibe.ui.fragments.payment.UiPayPasswordFragment
import com.hsvibe.ui.fragments.payment.UiPaymentFragment
import com.hsvibe.utilities.*
import com.hsvibe.viewmodel.LoginViewModel
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/7/4.
 */
class UiMainActivity : BaseActivity<ActivityMainBinding>(),
    TabLayout.OnTabSelectedListener,
    UserTokenManager.TokenStatusListener {

    private val mainViewModel by viewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }

    private val loginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    private var lastTabIndex = 0

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun getContainerId(): Int = R.id.fragment_container

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        L.d(TAG, "onNewIntent!!! action: ${intent?.action}")
    }

    override fun init() {
        checkLocationSetting()
        initTabLayout()
        startObserving()

        lifecycleScope.launchWhenResumed {
            checkUserToken()
        }
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
        openTabFragment(TabFragmentManager.TAG_HOME)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            TabFragmentManager.INDEX_HOME -> openTabFragment(TabFragmentManager.TAG_HOME)
            TabFragmentManager.INDEX_EXPLORE -> openTabFragment(TabFragmentManager.TAG_EXPLORE)
            TabFragmentManager.INDEX_COUPON -> openTabFragment(TabFragmentManager.TAG_COUPON)
            TabFragmentManager.INDEX_WALLET -> checkPasswordBeforeGoToNext(true)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        lastTabIndex = tab?.position ?: 0
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    private fun checkUserToken() {
        UserTokenManager.checkUserTokenStatus(this)
    }

    override fun onTokenOk() {
        L.i("onTokenOk!!!")
        setupUserInfoFromDb()
        runUserInfoSynchronize()
    }

    override fun onTokenExpiringSoon() {
        L.i("onTokenExpiringSoon!!!")
        setupUserInfoFromDb()
        refreshUserTokenAndUpdate()
    }

    override fun onTokenExpired() {
        L.i("onTokenExpired!!!")
        setupUserInfoFromDb()
        refreshUserTokenAndUpdate()
    }

    override fun onTokenNull() {
        L.i("onTokenNull!!!")
        mainViewModel.clearUserInfoFromDB()
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
                    is Navigation.ClickingBell -> openDialogFragment(UiNotificationFragment())
                    is Navigation.ClickPaymentCard -> checkPasswordBeforeGoToNext(false)
                    is Navigation.ClickingUserName -> checkBeforeGo(UiMemberCenterFragment())
                    is Navigation.OnAuthorizationFailed -> notifyAuthFailedAndGoToLogin()
                    is Navigation.OnLoginRequired -> showLoginRequireDialog()
                }
            }
            it.liveLoadingStatus.observe(this) { loadingStatus ->
                handleLoadingStatus(loadingStatus)
            }
        }
    }

    private fun setupUserInfoFromDb() {
        mainViewModel.setupUserInfoFromDb()
    }

    private fun checkLocationSetting() {
        if (hasLocationPermission()) {
            MyFusedLocation.checkLocationSetting(this@UiMainActivity) {
                mainViewModel.setLocationPermissionCheck(true)
            }
        } else {
            mainViewModel.setLocationPermissionCheck(false)
        }
    }

    private fun runUserInfoSynchronize() {
        mainViewModel.liveLocationPermissionChecked.observeOnce(this) {
            mainViewModel.runUserInfoSynchronize()
        }
    }

    private fun refreshUserTokenAndUpdate() {
        mainViewModel.liveLocationPermissionChecked.observeOnce(this) {
            mainViewModel.refreshTokenAndUpdate()
        }
    }

    private fun onMoreClick(apiType: Int) {
        when (apiType) {
            ApiConst.API_TYPE_NEWS -> {
                openDialogFragment(UiNewsFragment.newInstance())
            }
            ApiConst.API_TYPE_COUPON,
            ApiConst.API_TYPE_FOODS,
            ApiConst.API_TYPE_HOTEL -> {
                selectTab(TabFragmentManager.INDEX_COUPON)
            }
            ApiConst.API_TYPE_DISCOUNT -> {
                // This type doesn't have "more"!
            }
        }
    }

    private fun onNewsClick(itemIndex: Int) {
        openDialogFragment(UiNewsFragment.newInstance(itemIndex))
    }

    private fun onCouponClick(couponItem: ItemCoupon.ContentData) {
        openDialogFragment(UiCouponDetailFragment.newInstance(couponItem))
    }

    private fun onBannerClick(bannerItem: ItemBanner.ContentData) {
        openWebDialogFragment(bannerItem.share_url)
    }

    private fun checkBeforeGo(target: DialogFragment) {
        if (checkIsLoggedInAndProfileCompleted()) {
            openDialogFragment(target)
        }
    }

    private fun checkPasswordBeforeGoToNext(isWalletPage: Boolean) {
        if (checkIsLoggedInAndProfileCompleted()) {
            if (mainViewModel.isPasswordVerified()) {
                if (isWalletPage) {
                    openTabFragment(TabFragmentManager.TAG_WALLET)
                }
                else {
                    openDialogFragment(UiPaymentFragment())
                }
            }
            else {
                observePasswordVerification(isWalletPage)
                openDialogFragment(UiPayPasswordFragment.newInstance(mainViewModel.hasSetPayPassword().not()))
            }
        } else {
            selectTab(lastTabIndex)
        }
    }

    private fun observePasswordVerification(isWalletPage: Boolean) {
        mainViewModel.livePasswordVerified.observeOnce(this) { isVerified ->
            if (isVerified) {
                if (isWalletPage) {
                    openTabFragment(TabFragmentManager.TAG_WALLET)
                }
                else {
                    openDialogFragment(UiPaymentFragment())
                }
            }
            else {
                moveTabToHome()
            }
        }
    }

    private fun checkIsLoggedInAndProfileCompleted(): Boolean {
        return when {
            UserTokenManager.hasToken().not() -> {
                showLoginRequireDialog()
                false
            }
            mainViewModel.isUserInfoNotCompletely() -> {
                showCompleteInfoRequireDialog()
                false
            }
            else -> true
        }
    }

    private fun showLoginRequireDialog() {
        DialogHelper.showLargeViewDialog(
            this,
            R.string.require_login_title,
            R.string.require_login_content,
            R.string.login
        ) {
            openLoginPageAndObserve()
        }
    }

    private fun showCompleteInfoRequireDialog() {
        DialogHelper.showLargeViewDialog(
            this,
            R.string.require_complete_info_title,
            R.string.require_complete_info_content,
            R.string.input_member_info
        ) {
            openDialogFragment(UiMemberInfoFragment.newInstance(true))
        }
    }

    private fun openLoginPageAndObserve() {
        openDialogFragment(UiLoginWebDialogFragment(), tag = Const.TAG_DIALOG_LOGIN_WEB)

        loginViewModel.liveUserToken.observeOnce(this) { userToken ->
            dismissDialogFragment(Const.TAG_DIALOG_LOGIN_WEB)
            UserTokenManager.setUserToken(userToken)
            UserTokenManager.checkUserTokenStatus(this)
        }
    }

    private fun isAtHomeTab(): Boolean {
        return bindingView.mainTabLayout.selectedTabPosition == TabFragmentManager.INDEX_HOME
    }

    private fun moveTabToHome() {
        selectTab(TabFragmentManager.INDEX_HOME)
    }

    private fun selectTab(tabIndex: Int) {
        bindingView.mainTabLayout.getTabAt(tabIndex)?.select()
    }

    private fun notifyAuthFailedAndGoToLogin() {
        launch {
            Utility.toastLong(R.string.authorization_error)
            UserTokenManager.clearUserToken()
            SettingManager.setFullProfileIsAlreadyAsked(false)
            delay(3000)
            this@UiMainActivity.startActivitySafelyAndFinish(Intent(this@UiMainActivity, UiLoginActivity::class.java))
        }
    }

    // onActivityResult has been deprecated (ˊ_>ˋ)
    @Suppress("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // This is for the callback of checkLocationSetting's dialog from MyFusedLocation.
        if (requestCode == MyFusedLocation.REQUEST_CHECK_LOCATION_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    launch {
                        delay(2000)
                        mainViewModel.setLocationPermissionCheck(true)
                    }
                }
                Activity.RESULT_CANCELED -> mainViewModel.setLocationPermissionCheck(false)
            }
        }
    }

    override fun onBackPressed() {
        if (isAtHomeTab()) {
            super.onBackPressed()
        }
        else {
            moveTabToHome()
        }
    }
}