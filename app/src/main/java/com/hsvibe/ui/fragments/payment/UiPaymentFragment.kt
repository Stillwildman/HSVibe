package com.hsvibe.ui.fragments.payment

import android.view.Window
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPaymentBinding
import com.hsvibe.events.Events
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseDialogFragment
import com.hsvibe.ui.fragments.coupons.UiCouponHistoryFragment
import com.hsvibe.utilities.*
import com.hsvibe.viewmodel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        setCloseClickListener()
        startObserving()
        loadCreditCardAndPoints()
    }

    private fun setCloseClickListener() {
        bindingView.buttonClose.setOnSingleClickListener {
            popBack()
        }
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
                checkCardsAndOpenSelectionPage()
            }

            layoutPointDiscount.setOnSingleClickListener {
                openDialogFragment(UiPointSelectionFragment())
            }

            layoutUseCoupon.setOnSingleClickListener {
                mainViewModel.isCouponSelectingMode = true
                openDialogFragment(UiCouponHistoryFragment(), Const.BACK_COUPON_LiST)
            }
        }
    }

    private fun checkCardsAndOpenSelectionPage() {
        mainViewModel.liveCreditCards.value?.let {
            if (it.cardData.cardDetailList.isNotEmpty()) {
                openDialogFragment(UiCardSelectionFragment())
            }
            else {
                Utility.toastShort(R.string.please_bind_credit_card_first)
                openDialogFragment(UiBindCardWebFragment.newInstance(it.cardData.user_uuid))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBonusGet(event: Events.OnBonusGet) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            titleRes = R.string.transaction_completed,
            content = AppController.getAppContext().getString(R.string.transaction_amount_and_reward_hint, event.amount, event.rewardPoint)
        ) {
            popBack()
        }
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.isCouponSelectingMode = false
    }

    override fun onDialogBackPressed(): Boolean = false
}