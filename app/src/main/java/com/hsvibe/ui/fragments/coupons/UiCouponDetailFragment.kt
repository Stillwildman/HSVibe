package com.hsvibe.ui.fragments.coupons

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponDetailBinding
import com.hsvibe.model.Const
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.utilities.init
import com.hsvibe.viewmodel.CouponViewModel
import com.hsvibe.viewmodel.CouponViewModelFactory
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2021/8/11.
 */
class UiCouponDetailFragment private constructor() : BaseActionBarFragment<FragmentCouponDetailBinding>(),
    OnAnyItemClickCallback<ItemCoupon.ContentData>,
    SwipeRefreshLayout.OnRefreshListener
{
    companion object {
        fun newInstance(couponItem: ItemCoupon.ContentData): UiCouponDetailFragment {
            return UiCouponDetailFragment().apply {
                arguments = Bundle(1).also { it.putParcelable(Const.BUNDLE_COUPON_DATA, couponItem) }
            }
        }
    }

    private val couponViewModel by viewModels<CouponViewModel> { CouponViewModelFactory(CouponRepoImpl()) }
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_coupon_detail

    override fun getTitleRes(): Int? = null

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getMenuOptionIconRes(): Int = R.drawable.selector_share

    override fun onInitCompleted() {
        getBundleAndBind()
        observeLoadingStatus()
        binding.layoutSwipeRefresh.init(this)
    }

    private fun getBundleAndBind() {
        val couponItem = arguments?.getParcelable<ItemCoupon.ContentData>(Const.BUNDLE_COUPON_DATA)

        couponItem?.let {
            setTitle(it.title)
            couponViewModel.liveCouponDetail.value = it
            binding.viewModel = this.couponViewModel
            binding.itemClickCallback = this
            binding.lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun observeLoadingStatus() {
        couponViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    override fun onMenuOptionClick() {
        // TODO Share coupon info
    }

    override fun onItemClick(item: ItemCoupon.ContentData) {
        checkBalance(item)
    }

    private fun checkBalance(item: ItemCoupon.ContentData) {
        if (UserTokenManager.hasToken()) {
            mainViewModel.getCurrentUserBonus()?.let { userBonus ->
                if (userBonus.balance >= item.point) {
                    showRedeemConfirmationDialog(item.uuid, item.point)
                }
                else {
                    showInsufficientBalanceDialog()
                }
            }
        }
        else {
            mainViewModel.requireLogin()
        }
    }

    private fun showInsufficientBalanceDialog() {
        DialogHelper.showSingleButtonDialog(
            getContextSafely(),
            R.string.app_name,
            R.string.insufficient_balance
        )
    }

    private fun showRedeemConfirmationDialog(uuid: String, point: Int) {
        DialogHelper.showSmallViewDialog(
            getContextSafely(),
            AppController.getString(R.string.redeem_coupon),
            AppController.getAppContext().getString(R.string.confirm_to_redeem, point),
            R.string.confirm,
            R.string.cancel
        ) {
            redeemCoupon(uuid)
        }
    }

    private fun redeemCoupon(uuid: String) {
        couponViewModel.redeemCoupon(uuid) { isSuccess ->
            if (isSuccess) {
                showRedeemSuccessDialog()
            }
            else {
                showRedeemFailedDialog()
            }
        }
    }

    private fun showRedeemSuccessDialog() {
        onRefresh()
        context?.let {
            DialogHelper.showSimpleHsVibeDialog(it,
                R.string.redeem_success,
                AppController.getString(R.string.coupon_usage_description),
                R.drawable.ic_check_white,
                R.string.check_it_out
            ) {
                openDialogFragment(UiCouponHistoryFragment())
            }
        }
    }

    private fun showRedeemFailedDialog() {
        context?.let {
            DialogHelper.showSimpleHsVibeDialog(it,
                R.string.error_occurs,
                AppController.getString(R.string.redeem_failed),
                R.drawable.ic_close_white,
                R.string.confirm
            ) {
                // Do nothing!
            }
        }
    }

    override fun onRefresh() {
        couponViewModel.liveCouponDetail.value?.let {
            couponViewModel.updateCouponDetail(it.uuid)
        }
        mainViewModel.getUserBonus() // Refresh user bonus on home page.
    }

    override fun showLoadingCircle() {
        binding.layoutSwipeRefresh.isRefreshing = true
    }

    override fun hideLoadingCircle() {
        binding.layoutSwipeRefresh.isRefreshing = false
    }
}