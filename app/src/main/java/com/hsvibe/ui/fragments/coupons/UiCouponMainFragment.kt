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
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemBrand
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.adapters.CouponBrandListAdapter
import com.hsvibe.ui.adapters.CouponListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.getPairSecondValue
import com.hsvibe.utilities.init
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.CouponViewModel
import com.hsvibe.viewmodel.CouponViewModelFactory
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Vincent on 2021/8/16.
 */
class UiCouponMainFragment private constructor(): BaseFragment<FragmentCouponMainPageBinding>(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModels<CouponViewModel> { CouponViewModelFactory(CouponRepoImpl()) }

    companion object {
        /**
         * @param storeIds Set null or empty means ALL.
         */
        fun newInstance(storeIds: String? = null): UiCouponMainFragment {
            return UiCouponMainFragment().apply {
                arguments = Bundle().also { it.putString(Const.BUNDLE_STORE_ID, storeIds) }
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
        observeCouponBrands()
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
            val storeIds = arguments?.getString(Const.BUNDLE_STORE_ID)

            viewModel.getCouponFlow(storeIds).collectLatest { pagingCouponDataList ->
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
                bindingView.spinnerRegion.getPairSecondValue().takeIf { it.isNotEmpty() }?.let { getCouponBrands(it.toInt()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        bindingView.buttonSwitch.setOnSingleClickListener {
            //switchCategoryLayoutOrientation()  // TODO Fix this function!
        }
    }

    private fun getCouponBrands(categoryId: Int) {
        viewModel.getCouponBrands(categoryId)
    }

    private fun observeCouponBrands() {
        viewModel.liveCouponBrand.observe(viewLifecycleOwner) {
            updateBrandsRecycler(it.contentData)
        }
    }

    private fun updateBrandsRecycler(brandList: MutableList<ItemBrand.ContentData>) {
        bindingView.recyclerCouponBrands.apply {
            if (layoutManager == null || layoutManager?.isAttachedToWindow == false) {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
            if (adapter == null) {
                adapter = CouponBrandListAdapter(layoutManager as LinearLayoutManager, brandList, onStoreClickCallback, viewLifecycleOwner.lifecycleScope)
                doFirstSelection(brandList, false)
            }
            else {
                getStoreListAdapter()?.updateList(brandList)
                doFirstSelection(brandList, true)
            }
        }
    }

    private val onStoreClickCallback = object : OnAnyItemClickCallback<ItemBrand.ContentData> {
        override fun onItemClick(item: ItemBrand.ContentData) {
            getStoreListAdapter()?.setSelected(item)
            onBrandSelected(item.store_ids)
        }
    }

    private fun doFirstSelection(brandList: List<ItemBrand.ContentData>, refreshStoreIds: Boolean) {
        brandList.takeIf { it.isNotEmpty() }?.let {
            getStoreListAdapter()?.setSelected(it.first())
            if (refreshStoreIds) {
                onBrandSelected(it.first().store_ids)
            }
        }
    }

    private fun onBrandSelected(storeIds: String?) {
        viewModel.refreshCouponFlowByStoreIds(storeIds)
    }

    private fun switchCategoryLayoutOrientation() {
        getStoreListAdapter()?.changeLayoutOrientation()
    }

    private fun getStoreListAdapter(): CouponBrandListAdapter? {
        return bindingView.recyclerCouponBrands.adapter as? CouponBrandListAdapter
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