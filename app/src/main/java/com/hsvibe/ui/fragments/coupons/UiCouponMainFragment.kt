package com.hsvibe.ui.fragments.coupons

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponMainPageBinding
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemCouponStores
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.adapters.CouponListAdapter
import com.hsvibe.ui.adapters.CouponStoreListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.Extensions.getPairSecondValue
import com.hsvibe.utilities.Extensions.init
import com.hsvibe.utilities.Extensions.setOnSingleClickListener
import com.hsvibe.viewmodel.CouponViewModel
import com.hsvibe.viewmodel.CouponViewModelFactory
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Vincent on 2021/8/16.
 */
class UiCouponMainFragment private constructor(): BaseFragment<FragmentCouponMainPageBinding>(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModels<CouponViewModel> { CouponViewModelFactory(CouponRepoImpl()) }

    companion object {
        fun newInstance(storeId: Int): UiCouponMainFragment {
            return UiCouponMainFragment().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_STORE_ID, storeId) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_coupon_main_page

    override fun getLoadingView(): View? = null

    override fun init() {
        setupBinding()
        initSwipeRefreshLayout()
        initCouponRecycler()
        startObserveLoadingStatus()
        startCollectCouponFlow()
        getCouponDistricts()
        setSelectedListener()
        observeCouponStores()
    }

    private fun setupBinding() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = this
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.init(this)
    }

    private fun initCouponRecycler() {
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
            // TODO Share Coupon!
        }
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun startCollectCouponFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val storeId = arguments?.getInt(Const.BUNDLE_STORE_ID) ?: ApiConst.ALL

            viewModel.getCouponFlow(storeId).collectLatest { pagingCouponDataList ->
                getCouponListAdapter().submitData(pagingCouponDataList)
            }
        }
    }

    private fun getCouponDistricts() {
        viewModel.getCouponDistricts()
    }

    private fun setSelectedListener() {
        bindingView.spinnerRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bindingView.spinnerRegion.getPairSecondValue().takeIf { it.isNotEmpty() }?.let { getCouponStores(it.toInt()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        bindingView.buttonSwitch.setOnSingleClickListener {
            switchCategoryLayoutOrientation()
        }
    }

    private fun getCouponStores(categoryId: Int) {
        viewModel.getCouponStores(categoryId)
    }

    private fun observeCouponStores() {
        viewModel.liveCouponStores.observe(viewLifecycleOwner) {
            updateStoresRecycler(it.contentData)
        }
    }

    private fun updateStoresRecycler(storeList: List<ItemCouponStores.ContentData>) {
        bindingView.recyclerCouponStores.apply {
            if (layoutManager == null || layoutManager?.isAttachedToWindow == false) {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
            if (adapter == null) {
                adapter = CouponStoreListAdapter(layoutManager as LinearLayoutManager, storeList.toMutableList(), onStoreClickCallback, viewLifecycleOwner.lifecycleScope)
            }
            else {
                getStoreListAdapter()?.updateList(storeList)
            }
        }
    }

    private val onStoreClickCallback = object : OnAnyItemClickCallback<ItemCouponStores.ContentData> {
        override fun onItemClick(item: ItemCouponStores.ContentData) {
            getStoreListAdapter()?.setSelected(item)
            onStoreSelected(item.id)
        }
    }

    private fun onStoreSelected(storeId: Int) {
        viewModel.refreshCouponFlowByStoreId(storeId)
    }

    private fun switchCategoryLayoutOrientation() {
        getStoreListAdapter()?.changeLayoutOrientation()
    }

    private fun getStoreListAdapter(): CouponStoreListAdapter? {
        return bindingView.recyclerCouponStores.adapter as? CouponStoreListAdapter
    }

    private fun getCouponListAdapter(): CouponListAdapter {
        return bindingView.recyclerCouponList.adapter as CouponListAdapter
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

    override fun onBackButtonPressed(): Boolean = false
}