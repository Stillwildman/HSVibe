package com.hsvibe.ui.fragments.member

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponListBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.ui.bases.BaseFragment

/**
 * Created by Vincent on 2021/8/13.
 */
class UiCouponListPage private constructor() : BaseFragment<FragmentCouponListBinding>(),
    SwipeRefreshLayout.OnRefreshListener,
    OnAnyItemClickCallback<ItemCoupon.ContentData> {

    companion object {
        fun newInstance(viewType: Int): UiCouponListPage {
            return UiCouponListPage().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_VIEW_TYPE, viewType) }
            }
        }
    }

    //private val viewModel by viewModels<ContentViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_coupon_list

    override fun getLoadingView(): View? = null

    override fun init() {
        initSwipeRefreshLayout()
        initRecycler()
        startObserveLoadingStatus()
        startCollectContentFlow()
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.apply {
            setColorSchemeResources(
                R.color.md_green_500, R.color.md_amber_400,
                R.color.md_light_blue_A700, R.color.md_red_500
            )
            setOnRefreshListener(this@UiCouponListPage)
        }
    }

    private fun initRecycler() {
        bindingView.recyclerCouponList.apply {
            layoutManager = LinearLayoutManager(context)
            //adapter = NotificationListAdapter(this@UiCouponListPage)
        }
    }

    private fun startObserveLoadingStatus() {
//        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
//            handleLoadingStatus(it)
//        }
    }

    override fun showLoading() {
        showRefreshing()
    }

    override fun hideLoading() {
        hideRefreshing()
    }

    private fun showRefreshing() {
        bindingView.layoutSwipeRefresh.isRefreshing = true
    }

    private fun hideRefreshing() {
        bindingView.layoutSwipeRefresh.isRefreshing = false
    }

    private fun startCollectContentFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
//            viewModel.getContentFlow(category).collectLatest { pagingContentDataList ->
//                getCouponListAdapter().submitData(pagingContentDataList)
//            }
        }
    }

//    private fun getCouponListAdapter(): NotificationListAdapter {
//        return bindingView.recyclerCouponList
//    }

    override fun onRefresh() {
//        getCouponListAdapter().refresh()
    }

    override fun onItemClick(item: ItemCoupon.ContentData) {
        // TODO On each coupon click?
    }

    override fun onBackButtonPressed(): Boolean = true
}