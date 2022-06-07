package com.hsvibe.ui.fragments.payment

import android.content.res.TypedArray
import android.view.View
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentWalletMainBinding
import com.hsvibe.model.Const
import com.hsvibe.model.actions.CreditCardAction
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.ui.adapters.CreditCardPageAdapter
import com.hsvibe.ui.adapters.WalletMenuAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.ui.fragments.coupons.UiCouponHistoryFragment
import com.hsvibe.ui.fragments.member.UiPointHistoryFragment
import com.hsvibe.utilities.*
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2021/8/23.
 */
class UiWalletMainFragment : BaseFragment<FragmentWalletMainBinding>(), OnAnyItemClickCallback<Int> {

    override fun getLayoutId(): Int = R.layout.fragment_wallet_main

    override fun getLoadingView(): View? = null

    private val viewModel by activityViewModels<MainViewModel>()

    override fun init() {
        initWalletMenuRecycler()
        initCardPager()
        startObserving()
        loadCreditCardList()
    }

    private fun initWalletMenuRecycler() {
        bindingView.recyclerWalletMenu.apply {
            getInflatedSize { _, height ->
                val heightPerGrid: Int = height / 3
                layoutManager = GridLayoutManager(context, 3)
                adapter = WalletMenuAdapter(getMenuIconTextList(), heightPerGrid, this@UiWalletMainFragment)
            }
        }
    }

    private fun initCardPager(): CreditCardPageAdapter {
        return CreditCardPageAdapter(clickCallback = cardClickCallback).also { pageAdapter ->
            bindingView.pagerCards.apply {
                val recyclerView = getChildAt(0) as? RecyclerView
                recyclerView?.let {
                    val padding = AppController.getAppContext().resources.getDimensionPixelOffset(R.dimen.padding_size_xxl)
                    it.setPadding(padding, 0, padding, 0)
                    it.clipToPadding = false
                }
                val margin = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_l)
                setPageTransformer(MarginPageTransformer(margin))

                adapter = pageAdapter
            }
        }
    }

    private fun getMenuIconTextList(): List<Pair<Int, String>> {
        val iconArray: TypedArray = AppController.getAppContext().resources.obtainTypedArray(R.array.wallet_menu_icon_array)
        val textArray = AppController.getAppContext().resources.getStringArray(R.array.wallet_menu_text_array)

        val pairList = mutableListOf<Pair<Int, String>>()

        textArray.forEachIndexed { index, text ->
            pairList.add(Pair(iconArray.getResourceId(index, 0), text))
        }

        iconArray.recycle()

        return pairList
    }

    private fun startObserving() {
        viewModel.liveCreditCards.observe(viewLifecycleOwner) {
            updateCreditCardPager(it)
        }
    }

    private fun loadCreditCardList() {
        viewModel.loadCreditCards()
    }

    private fun updateCreditCardPager(cardListItem: ItemCardList) {
        L.i("updateCreditCardPager!! Size: ${cardListItem.cardData.cardDetailList.size}")
        getCardPagerAdapter()?.apply {
            updateList(cardListItem.cardData.cardDetailList)
        } ?: run {
            initCardPager().updateList(cardListItem.cardData.cardDetailList)
        }
        moveCardPagerToFirst()
    }

    private fun moveCardPagerToFirst() {
        bindingView.pagerCards.doOnLayout {
            bindingView.pagerCards.setCurrentItem(0, true)
        }
    }

    private val cardClickCallback by lazy { object : OnAnyItemClickCallback<CreditCardAction> {
        override fun onItemClick(item: CreditCardAction) {
            when (item) {
                is CreditCardAction.OnAddNewCardClick -> {
                    openCreditCardRegisterWeb()
                }
                is CreditCardAction.OnCreditCardClick -> {
                    showDefaultCardDialog(item.key)
                }
                is CreditCardAction.OnDeleteCardClick -> {
                    showDeleteCardDialog(item.key)
                }
            }
        }
    }}

    private fun openCreditCardRegisterWeb() {
        viewModel.liveCreditCards.value?.cardData?.user_uuid?.let { uuid ->
            openDialogFragment(UiBindCardWebFragment.newInstance(uuid))
        }
    }

    private fun showDefaultCardDialog(key: String) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            R.style.DialogSurfaceDark,
            R.string.hs_pay,
            AppController.getString(R.string.confirm_to_set_as_default),
            R.drawable.ic_app_sign,
            R.string.set_as_default,
            true
        ) {
            setupDefaultCard(key)
        }
    }

    private fun setupDefaultCard(key: String) {
        if (SettingManager.setDefaultCreditCardKey(key)) {
            viewModel.updateDefaultCreditCardIndex(key)
            getCardPagerAdapter()?.refresh()
        }
    }

    private fun showDeleteCardDialog(key: String) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            R.style.DialogSurfaceCaution,
            R.string.hs_pay,
            AppController.getString(R.string.confirm_to_delete_card),
            R.drawable.ic_delete_white,
            R.string.confirm_to_delete,
            true
        ) {
            observeCardDeleting()
            viewModel.deleteCreditCard(key)
        }
    }

    private fun observeCardDeleting() {
        viewModel.liveCreditCardDeleting.observeOnce(viewLifecycleOwner) {
            if (it.cardData.isOperationSuccess() && SettingManager.clearDefaultCreditCardKey()) {
                Utility.toastShort(it.cardData.message)
                viewModel.loadCreditCards()
            }
            else {
                showFailedDialog(it.cardData.message)
            }
        }
    }

    private fun showFailedDialog(message: String) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            R.style.DialogSurfaceDark,
            R.string.hs_pay,
            message,
            R.drawable.ic_close_white)
    }

    private fun getCardPagerAdapter(): CreditCardPageAdapter? {
        return bindingView.pagerCards.adapter as? CreditCardPageAdapter
    }

    override fun onItemClick(item: Int) {
        when (item) {
            R.drawable.ic_my_qr_code -> openDialogFragment(UiPaymentFragment())
            R.drawable.ic_add_card ->  openCreditCardRegisterWeb()
            R.drawable.ic_passcode -> openDialogFragment(UiPayPasswordFragment.newInstance(true))
            R.drawable.ic_point_transfer -> {  }  // TODO Finish this!
            R.drawable.ic_point_history -> openDialogFragment(UiPointHistoryFragment())
            R.drawable.ic_ticket_holder -> openDialogFragment(UiCouponHistoryFragment(), Const.BACK_COUPON_LiST)
            R.drawable.ic_transaction_history -> openDialogFragment(UiTransactionHistoryFragment())
            R.drawable.ic_scan -> { // TODO Temporary Disabled
                checkPermissionThenOpenDialogFragment(PermissionCheckHelper.PERMISSION_REQUEST_CODE_CAMERA, UiScanFragment.newInstance(false))
            }
        }
    }

    override fun onBackButtonPressed(): Boolean = false
}