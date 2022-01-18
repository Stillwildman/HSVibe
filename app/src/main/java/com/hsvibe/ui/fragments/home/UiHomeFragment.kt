package com.hsvibe.ui.fragments.home

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsvibe.R
import com.hsvibe.databinding.FragmentHomeBinding
import com.hsvibe.repositories.HomeContentRepoImpl
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.adapters.HomeContentListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.ui.fragments.member.UiMemberInfoFragment
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.viewmodel.HomeViewModel
import com.hsvibe.viewmodel.HomeViewModelFactory
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory
import kotlinx.coroutines.delay

/**
 * Created by Vincent on 2021/7/4.
 */
class UiHomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }
    private val homeViewModel by viewModels<HomeViewModel> { HomeViewModelFactory(HomeContentRepoImpl(), mainViewModel) }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun init() {
        setupBinding()
        initRecycler()
        observeScrollingForCornerCard()
        startObserving()

        getData()
    }

    private fun setupBinding() {
        bindingView.apply {
            homeViewModel = this@UiHomeFragment.homeViewModel
            mainViewModel = this@UiHomeFragment.mainViewModel
            lifecycleOwner = this@UiHomeFragment
        }
    }

    private fun initRecycler() {
        bindingView.recyclerHome.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = HomeContentListAdapter(homeViewModel)
        }
    }

    private fun observeScrollingForCornerCard() {
        bindingView.layoutHomeScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            bindingView.imageCornerCard.scrollTo(-1 * scrollY, scrollY)
        })
    }

    private fun startObserving() {
        mainViewModel.liveUserInfo.observe(viewLifecycleOwner) { userInfo ->
            L.i(TAG, "UserInfo Get!!!\n${userInfo.getLogInfo()}")
            askForCompleteInfoNeeds()
        }

        homeViewModel.let {
            it.liveLoadingStatus.observe(viewLifecycleOwner) { loadingStatus ->
                handleLoadingStatus(loadingStatus)
            }
            it.liveNews.observe(viewLifecycleOwner) {
                notifyDataChanged(HomeContentListAdapter.LIST_POSITION_NEWS)
            }
            it.liveCoupons.observe(viewLifecycleOwner) {
                notifyDataChanged(HomeContentListAdapter.LIST_POSITION_COUPON)
            }
            it.liveBanner.observe(viewLifecycleOwner) {
                notifyDataChanged(HomeContentListAdapter.LIST_POSITION_DISCOUNT)
            }
        }
    }

    private fun askForCompleteInfoNeeds() {
        if (mainViewModel.isUserInfoNotCompletely() && SettingManager.isNeedToAskForFullProfile()) {
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                delay(2000)
                DialogHelper.showLargeViewDialog(
                    getContextSafely(),
                    R.string.hint_complete_info_title,
                    R.string.hint_complete_info_content,
                    R.string.input_member_info,
                    R.string.later,
                ) {
                    openDialogFragment(UiMemberInfoFragment.newInstance(true))
                }
                SettingManager.setFullProfileIsAlreadyAsked(true)
            }
        }
    }

    private fun getData() {
        homeViewModel.run {
            getHomePageNews()
            getHomePageCoupons()
            getHomePageBanner()
        }
    }

    private fun notifyDataChanged(dataPosition: Int) {
        (bindingView.recyclerHome.adapter as HomeContentListAdapter).notifyDataGet(dataPosition)
    }

    override fun onBackButtonPressed(): Boolean = false

}