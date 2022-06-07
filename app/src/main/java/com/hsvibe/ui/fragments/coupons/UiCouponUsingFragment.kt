package com.hsvibe.ui.fragments.coupons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCouponUsingBinding
import com.hsvibe.events.Events
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.repositories.CouponRepoImpl
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.utilities.observeOnce
import com.hsvibe.viewmodel.CouponViewModel
import com.hsvibe.viewmodel.CouponViewModelFactory
import com.hsvibe.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Vincent on 2022/4/19.
 */
class UiCouponUsingFragment private constructor() : BaseActionBarFragment<FragmentCouponUsingBinding>(), OnAnyItemClickCallback<String> {

    companion object {
        fun newInstance(couponItem: ItemMyCoupon.ContentData): UiCouponUsingFragment {
            return UiCouponUsingFragment().apply {
                arguments = Bundle(1).also { it.putParcelable(Const.BUNDLE_COUPON_DATA, couponItem) }
            }
        }
    }

    private val couponViewModel by viewModels<CouponViewModel> { CouponViewModelFactory(CouponRepoImpl()) }

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_coupon_using

    override fun getTitleRes(): Int? = null

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    private var isCouponSelectingMode = false

    private val originBrightness by lazy { activity?.window?.attributes?.screenBrightness ?: 0.5f }

    override fun onInitCompleted() {
        getBundleAndBind()
        startObserveLoadingStatus()
        observeCouponCode()
    }

    private fun getBundleAndBind() {
        val couponItem = arguments?.getParcelable<ItemMyCoupon.ContentData>(Const.BUNDLE_COUPON_DATA)

        couponItem?.let {
            setTitle(it.title)
            binding.coupon = it
            binding.itemClickCallback = this
        }
    }

    private fun startObserveLoadingStatus() {
        couponViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun observeCouponCode() {
        couponViewModel.liveCouponCode.observeOnce(viewLifecycleOwner) {
            bindBarcode(it)
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onItemClick(uuid: String) {
        couponViewModel.useCoupon(uuid)
    }

    private fun bindBarcode(couponCode: String) {
        binding.barcodeText = couponCode
        tuneScreenBrightness(true)

        setSelectedCouponIfNeed()
    }

    private fun setSelectedCouponIfNeed() {
        if (mainViewModel.isCouponSelectingMode) {
            binding.coupon?.let {
                isCouponSelectingMode = true
                mainViewModel.updatePaymentCoupon(it.title, it.uuid)
            }
        }
    }

    private fun isBarcodeTurnedOn(): Boolean {
        return binding.imageBarcode.visibility == View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        tuneScreenBrightness(isBarcodeTurnedOn())

        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        tuneScreenBrightness(false)

        EventBus.getDefault().unregister(this)
    }

    private fun tuneScreenBrightness(maximize: Boolean) {
        activity?.window?.attributes?.let { attr ->
            if (maximize) {
                attr.screenBrightness = 1f
            }
            else {
                attr.screenBrightness = originBrightness
            }
            activity?.window?.attributes = attr
        }
    }

    override fun showLoadingCircle() {
        binding.barcodeLoadingCircle.visibility = View.VISIBLE
    }

    override fun hideLoadingCircle() {
        binding.barcodeLoadingCircle.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCouponUsed(event: Events.OnCouponUsed) {
        binding.coupon?.let {
            DialogHelper.showHsVibeDialog(
                getContextSafely(),
                titleRes = R.string.use_coupon,
                content = AppController.getAppContext().getString(R.string.coupon_scanned_hint, it.title),
                positiveButtonRes = R.string.confirm
            ) {
                popBack(Const.BACK_COUPON_LiST)
            }
        }
    }

    override fun onDialogBackPressed(): Boolean {
        return if (isCouponSelectingMode) {
            popBack(Const.BACK_COUPON_LiST)
            true
        } else {
            super.onDialogBackPressed()
        }
    }
}