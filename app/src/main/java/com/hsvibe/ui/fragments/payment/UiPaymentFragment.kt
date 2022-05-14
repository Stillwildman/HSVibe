package com.hsvibe.ui.fragments.payment

import android.view.Window
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPaymentBinding
import com.hsvibe.ui.bases.BaseDialogFragment

/**
 * Created by Vincent on 2022/5/14.
 */
class UiPaymentFragment : BaseDialogFragment<FragmentPaymentBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_payment

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun canCanceledOnTouchOutside(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setDialogWindowAttrs(window: Window) {
        TODO("Not yet implemented")
    }

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun onDialogBackPressed(): Boolean {
        TODO("Not yet implemented")
    }
}