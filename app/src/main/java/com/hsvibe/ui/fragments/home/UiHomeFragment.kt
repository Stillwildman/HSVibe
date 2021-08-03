package com.hsvibe.ui.fragments.home

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsvibe.R
import com.hsvibe.databinding.FragmentHomeBinding
import com.hsvibe.repositories.ContentRepoImpl
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.adapters.ContentListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import com.hsvibe.viewmodel.HomeViewModel
import com.hsvibe.viewmodel.HomeViewModelFactory
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory

/**
 * Created by Vincent on 2021/7/4.
 */
class UiHomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }
    private val homeViewModel by viewModels<HomeViewModel> { HomeViewModelFactory(ContentRepoImpl(), mainViewModel) }

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
        bindingView.mainViewModel = mainViewModel
        bindingView.lifecycleOwner = this
    }

    private fun initRecycler() {
        bindingView.recyclerHome.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = ContentListAdapter(homeViewModel)
        }
    }

    private fun observeScrollingForCornerCard() {
        bindingView.layoutHomeScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            bindingView.imageCornerCard.scrollTo(-1 * scrollY, scrollY)
        })
    }

    private fun startObserving() {
        mainViewModel.liveUserInfo.observe(viewLifecycleOwner) { userInfo ->
            L.i("UserInfo Get!!!\n${userInfo.getLogInfo()}")
        }

        homeViewModel.let {
            it.liveLoadingStatus.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) showLoading() else hideLoading()
            }
            it.liveErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                Utility.toastLong(errorMessage)
            }
            it.liveNews.observe(viewLifecycleOwner) {
                notifyDataChanged(ContentListAdapter.LIST_POSITION_NEWS)
            }

            it.liveCoupons.observe(viewLifecycleOwner) {
                notifyDataChanged(ContentListAdapter.LIST_POSITION_COUPON)
            }
            it.liveBanner.observe(viewLifecycleOwner) {
                notifyDataChanged(ContentListAdapter.LIST_POSITION_DISCOUNT)
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
        (bindingView.recyclerHome.adapter as ContentListAdapter).notifyDataGet(dataPosition)
    }

    override fun onBackButtonPressed(): Boolean = false

}