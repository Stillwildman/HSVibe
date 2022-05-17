package com.hsvibe.ui.fragments.payment

import android.view.Window
import androidx.fragment.app.activityViewModels
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPaymentBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseDialogFragment
import com.hsvibe.ui.fragments.coupons.UiCouponHistoryFragment
import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.observeOnce
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2022/5/14.
 */
class UiPaymentFragment : BaseDialogFragment<FragmentPaymentBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_payment

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {}

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun init() {
        startObserving()
        loadCreditCardAndPoints()
    }

    private fun startObserving() {
        mainViewModel.liveCreditCards.observeOnce(viewLifecycleOwner) {
            updatePaymentDisplayAndBind()
        }
    }

    private fun loadCreditCardAndPoints() {
        mainViewModel.loadCreditCards()
        mainViewModel.loadUserBonus()
    }

    private fun updatePaymentDisplayAndBind() {
        mainViewModel.initPaymentDisplay()
        mainViewModel.loadPaymentCode()

        bindingView.apply {
            viewModel = mainViewModel
            lifecycleOwner = this@UiPaymentFragment

            switchCreditCard.setOnCheckedChangeListener { _, isChecked ->
                SettingManager.setCreditCardPaymentEnabled(isChecked)
                mainViewModel.updatePaymentMethod(isCreditCardEnabled = isChecked)
            }

            switchPoint.setOnCheckedChangeListener { _, isChecked ->
                SettingManager.setPointPaymentEnabled(isChecked)
                mainViewModel.updatePaymentMethod(isPointEnabled = isChecked)
            }

            layoutHsPay.setOnSingleClickListener {
                // TODO
            }

            layoutPointDiscount.setOnSingleClickListener {
                // TODO
            }

            layoutUseCoupon.setOnSingleClickListener {
                mainViewModel.isCouponSelectingMode = true
                openDialogFragment(UiCouponHistoryFragment(), Const.BACK_COUPON_LiST)
            }

            buttonClose.setOnSingleClickListener {
                popBack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.isCouponSelectingMode = false
    }

    override fun onDialogBackPressed(): Boolean = false
}