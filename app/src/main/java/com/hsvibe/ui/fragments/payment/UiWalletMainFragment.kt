package com.hsvibe.ui.fragments.payment

import android.content.res.TypedArray
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentWalletMainBinding
import com.hsvibe.ui.adapters.WalletMenuAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.ui.fragments.coupons.UiCouponHistoryFragment
import com.hsvibe.ui.fragments.member.UiPointHistoryFragment
import com.hsvibe.utilities.PermissionCheckHelper
import com.hsvibe.utilities.getInflatedSize

/**
 * Created by Vincent on 2021/8/23.
 */
class UiWalletMainFragment : BaseFragment<FragmentWalletMainBinding>(), OnAnyItemClickCallback<Int> {

    override fun getLayoutId(): Int = R.layout.fragment_wallet_main

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun init() {
        initWalletMenuRecycler()
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

    // TODO Finish menu options.
    override fun onItemClick(item: Int) {
        when (item) {
            R.drawable.ic_my_qr_code -> {}
            R.drawable.ic_add_card -> {}
            R.drawable.ic_passcode -> openDialogFragment(UiPasswordFragment.newInstance(true))
            R.drawable.ic_point_transfer -> {}
            R.drawable.ic_point_history -> openDialogFragment(UiPointHistoryFragment())
            R.drawable.ic_ticket_holder -> openDialogFragment(UiCouponHistoryFragment())
            R.drawable.ic_transaction_history -> {}
            R.drawable.ic_scan -> {
                checkPermissionThenOpenDialogFragment(PermissionCheckHelper.PERMISSION_REQUEST_CODE_CAMERA, UiScanFragment.newInstance(false))
            }
        }
    }

    override fun onBackButtonPressed(): Boolean = false
}