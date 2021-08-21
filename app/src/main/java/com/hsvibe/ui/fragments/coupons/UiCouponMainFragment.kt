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
import com.hsvibe.model.items.ItemCouponCategories
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.adapters.CouponCategoryListAdapter
import com.hsvibe.ui.adapters.CouponListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.Extensions.observeOnce
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
        fun newInstance(category: Int): UiCouponMainFragment {
            return UiCouponMainFragment().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_CATEGORY, category) }
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
        getCouponCategories()
        setSelectedListener()
        observeCategoryList()
    }

    private fun setupBinding() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = this
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
            val category = arguments?.getInt(Const.BUNDLE_CATEGORY) ?: ApiConst.CATEGORY_ALL

            viewModel.getCouponFlow(category).collectLatest { pagingCouponDataList ->
                getCouponListAdapter().submitData(pagingCouponDataList)
            }
        }
    }

    private fun getCouponCategories() {
        viewModel.run {
            getCouponDistricts()
            getCouponCategories()
        }
    }

    private fun setSelectedListener() {
        bindingView.spinnerRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO Change coupon category?
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        bindingView.buttonSwitch.setOnSingleClickListener {
            switchCategoryLayoutOrientation()
        }
    }

    private fun observeCategoryList() {
        viewModel.liveCouponCategories.observeOnce(viewLifecycleOwner) {
            initCategoryRecycler(it.contentData)
        }
    }

    private fun initCategoryRecycler(categoryList: List<ItemCouponCategories.ContentData>) {
        val category = arguments?.getInt(Const.BUNDLE_CATEGORY) ?: ApiConst.CATEGORY_ALL

        bindingView.recyclerCouponCategory.apply {
            layoutManager = LinearLayoutManager(context, if (category == ApiConst.CATEGORY_ALL) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL, false)
            adapter = CouponCategoryListAdapter(layoutManager as LinearLayoutManager, categoryList, onCategoryClickCallback, viewLifecycleOwner.lifecycleScope)
        }
    }

    private val onCategoryClickCallback = object : OnAnyItemClickCallback<ItemCouponCategories.ContentData> {
        override fun onItemClick(item: ItemCouponCategories.ContentData) {
            getCategoryListAdapter()?.setSelected(item)
        }
    }

    private fun switchCategoryLayoutOrientation() {
        getCategoryListAdapter()?.changeLayoutOrientation()
    }

    private fun getCategoryListAdapter(): CouponCategoryListAdapter? {
        return bindingView.recyclerCouponCategory.adapter as? CouponCategoryListAdapter
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