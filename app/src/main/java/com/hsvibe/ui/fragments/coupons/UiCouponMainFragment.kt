package com.hsvibe.ui.fragments.coupons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponMainPageBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.ui.adapters.CouponListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.viewmodel.CouponViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Vincent on 2021/8/16.
 */
class UiCouponMainFragment private constructor(): BaseFragment<FragmentCouponMainPageBinding>(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModels<CouponViewModel>()

    companion object {
        fun newInstance(category: Int): UiCouponMainFragment {
            return UiCouponMainFragment().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_CATEGORY, category) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_coupon_main_page

    override fun getLoadingView(): View? = null

    override fun init() {
        initSwipeRefreshLayout()
        initRecyclers()
        startObserveLoadingStatus()
        startCollectCouponFlow()
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.apply {
            setColorSchemeResources(
                R.color.md_green_500, R.color.md_amber_400,
                R.color.md_light_blue_A700, R.color.md_red_500
            )
            setOnRefreshListener(this@UiCouponMainFragment)
        }
    }

    private fun initRecyclers() {
        bindingView.recyclerCouponCategory.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        }

        bindingView.recyclerCouponList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CouponListAdapter(onCouponClickCallback, onShareClickCallback)
        }
    }

    private val onCouponClickCallback = object : OnAnyItemClickCallback<ItemCoupon.ContentData> {
        override fun onItemClick(item: ItemCoupon.ContentData) {
            openDialogFragment(UiCouponDetailFragment.newInstance(item))
        }
    }

    private val onShareClickCallback = object : OnAnyItemClickCallback<ItemCoupon.ContentData> {
        override fun onItemClick(item: ItemCoupon.ContentData) {

        }
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun startCollectCouponFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val category = arguments?.getInt(Const.BUNDLE_CATEGORY) ?: 0

            viewModel.getCouponFlow(category).collectLatest { pagingCouponDataList ->
                getCouponListAdapter().submitData(pagingCouponDataList)
            }
        }
    }

    override fun showLoading() {
        bindingView.layoutSwipeRefresh.isRefreshing = true
    }

    override fun hideLoading() {
        bindingView.layoutSwipeRefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getCouponListAdapter().refresh()
    }

    private fun getCouponListAdapter(): CouponListAdapter {
        return bindingView.recyclerCouponList.adapter as CouponListAdapter
    }

    override fun onBackButtonPressed(): Boolean = false
}