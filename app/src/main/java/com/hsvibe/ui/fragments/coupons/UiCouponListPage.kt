package com.hsvibe.ui.fragments.coupons

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.callbacks.ViewModelInterface
import com.hsvibe.databinding.FragmentCouponListBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.ui.adapters.MyCouponListAdapter
import com.hsvibe.ui.adapters.MyCouponUsedListAdapter
import com.hsvibe.ui.bases.BaseBindingDiffRecycler
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.L
import com.hsvibe.utilities.init
import com.hsvibe.viewmodel.CouponViewModel

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

    private lateinit var viewModelInterface: ViewModelInterface<CouponViewModel>

    private val pageType by lazy { arguments?.getInt(Const.BUNDLE_VIEW_TYPE, Const.PAGE_MY_COUPON) }

    private val onCouponClickCallback by lazy { object : OnAnyItemClickCallback<ItemMyCoupon.ContentData> {
        override fun onItemClick(item: ItemMyCoupon.ContentData) {
            openDialogFragment(UiCouponUsingFragment.newInstance(item), Const.BACK_COUPON_LiST)
        }
    } }

    @Suppress("UNCHECKED_CAST")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            viewModelInterface = parentFragment as ViewModelInterface<CouponViewModel>
        }
        catch (e: ClassCastException) {
            e.printStackTrace()
            L.e(TAG, context.javaClass.simpleName + " must implement " + ViewModelInterface::class.java.simpleName)
        }
    }

    override fun init() {
        initSwipeRefreshLayout()
        initRecycler()
        startObserveLoadingStatus()
        observeCouponListByPageType()
        loadMyCouponListPair()
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.init(this)
    }

    private fun initRecycler() {
        bindingView.recyclerCouponList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = when (pageType) {
                Const.PAGE_MY_COUPON -> MyCouponListAdapter(onCouponClickCallback)
                Const.PAGE_USED_COUPON -> MyCouponUsedListAdapter()
                else -> null
            }
        }
    }

    private fun startObserveLoadingStatus() {
        viewModelInterface.viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun observeCouponListByPageType() {
        when (pageType) {
            Const.PAGE_MY_COUPON -> {
                viewModelInterface.viewModel.liveMyCouponList.observe(viewLifecycleOwner) {
                    setCouponList(it)
                }
            }
            Const.PAGE_USED_COUPON -> {
                viewModelInterface.viewModel.liveUsedCouponList.observe(viewLifecycleOwner) {
                    setCouponList(it)
                }
            }
        }
    }

    private fun setCouponList(couponList: List<ItemMyCoupon.ContentData>?) {
        couponList?.let {
            if (it.isNotEmpty()) {
                getCouponListAdapter()?.submitList(it)
            }
            else {
                showEmptyHint()
            }
        }
    }

    private fun loadMyCouponListPair() {
        viewModelInterface.viewModel.loadMyCouponListPair()
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
        viewModelInterface.viewModel.refreshMyCouponListPair()
    }

    override fun onBackButtonPressed(): Boolean = true
}