package com.hsvibe.ui.fragments.member

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponListBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.adapters.MyCouponListAdapter
import com.hsvibe.ui.bases.BaseBindingDiffRecycler
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.Utility
import com.hsvibe.utilities.init
import com.hsvibe.viewmodel.CouponViewModel
import com.hsvibe.viewmodel.CouponViewModelFactory

/**
 * Created by Vincent on 2021/8/13.
 */
class UiCouponListPage private constructor() : BaseFragment<FragmentCouponListBinding>(),
    SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance(viewType: Int): UiCouponListPage {
            return UiCouponListPage().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_VIEW_TYPE, viewType) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_coupon_list

    override fun getLoadingView(): View? = null

    private val pageType by lazy { arguments?.getInt(Const.BUNDLE_VIEW_TYPE, Const.PAGE_MY_COUPON) }

    private val viewModel by viewModels<CouponViewModel> { CouponViewModelFactory(CouponRepoImpl()) }

    private val onCouponClickCallback by lazy { object : OnAnyItemClickCallback<ItemMyCoupon.ContentData> {
        override fun onItemClick(item: ItemMyCoupon.ContentData) {
            Utility.toastShort(item.title)
        }
    } }

    override fun init() {
        initSwipeRefreshLayout()
        initRecycler()
        startObserveLoadingStatus()
        observeMyCouponList()
        loadMyCouponListByPageType()
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.init(this)
    }

    private fun initRecycler() {
        bindingView.recyclerCouponList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = when (pageType) {
                Const.PAGE_MY_COUPON -> MyCouponListAdapter(onCouponClickCallback)
                Const.PAGE_USED_COUPON -> { MyCouponListAdapter(onCouponClickCallback)  }
                else -> null
            }
        }
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun observeMyCouponList() {
        viewModel.liveMyCouponList.observe(viewLifecycleOwner) {
            getCouponListAdapter()?.submitList(it)
        }
    }

    private fun loadMyCouponListByPageType() {
        when (pageType) {
            Const.PAGE_MY_COUPON -> {
                viewModel.getMyCouponList(true) {
                    showEmptyHint()
                }
            }
            Const.PAGE_USED_COUPON -> {
                viewModel.getMyCouponList(false) {
                    showEmptyHint()
                }
            }
        }
    }



    private fun showEmptyHint() {
        // TODO Empty hint?
    }

    override fun showLoading() {
        bindingView.layoutSwipeRefresh.isRefreshing = true
    }

    override fun hideLoading() {
        bindingView.layoutSwipeRefresh.isRefreshing = false
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCouponListAdapter(): BaseBindingDiffRecycler<ItemMyCoupon.ContentData, *>? {
        return bindingView.recyclerCouponList.adapter as? BaseBindingDiffRecycler<ItemMyCoupon.ContentData, *>
    }

    override fun onRefresh() {
        loadMyCouponListByPageType()
    }

    override fun onBackButtonPressed(): Boolean = true
}