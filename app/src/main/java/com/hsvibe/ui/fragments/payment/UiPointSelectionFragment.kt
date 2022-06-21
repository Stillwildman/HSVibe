package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPointsSelectionBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.*
import com.hsvibe.viewmodel.MainViewModel
import java.text.NumberFormat
import java.text.ParseException

/**
 * Created by Vincent on 2022/5/20.
 */
class UiPointSelectionFragment private constructor() : BaseActionBarFragment<FragmentPointsSelectionBinding>() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_points_selection

    override fun getTitleRes(): Int = R.string.using_points

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var currentPoints = 0

    private val numberFormat by lazy { NumberFormat.getInstance() }

    private val phoneNumber by lazy { arguments?.getString(Const.BUNDLE_PHONE_NUMBER) }

    private val isFromPayment by lazy { phoneNumber == null }

    companion object {
        fun newInstance(phoneNumber: String? = null): UiPointSelectionFragment {
            return UiPointSelectionFragment().apply {
                arguments = Bundle(1).also { it.putString(Const.BUNDLE_PHONE_NUMBER, phoneNumber) }
            }
        }
    }

    override fun onInitCompleted() {
        getCurrentPointsAndBind()
        setListeners()
    }

    private fun getCurrentPointsAndBind() {
        currentPoints = mainViewModel.getCurrentUserBalance()

        binding.totalPoints = numberFormat.format(currentPoints)

        if (isFromPayment) {
            mainViewModel.livePaymentDisplay.observeOnce(viewLifecycleOwner) {
                binding.selectedPoints = numberFormat.format(
                    if (it.isPointEnabled && it.selectedPoints > 0) it.selectedPoints else currentPoints
                )
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            editPointInput.addTextChangedListener {
                it?.let { applyInputNumber(it.toString()) }
            }

            buttonPlus.setOnClickListener {
                changeInputNumber(true)
            }

            buttonMinus.setOnClickListener {
                changeInputNumber(false)
            }

            buttonConfirm.setOnSingleClickListener {
                checkThenCommitPoints()
            }
        }
    }

    private fun applyInputNumber(number: String) {
        binding.selectedPoints = number

        if (number.isEmpty()) {
            binding.editPointInput.text.clear()
        }
    }

    private fun changeInputNumber(isPlus: Boolean) {
        if (isPlus) {
            val changedNumber = getInputNumber() + 1
            if (changedNumber <= currentPoints) {
                binding.selectedPoints = numberFormat.format(changedNumber)
            }
            else {
                Utility.toastShort(R.string.point_limit)
            }
        }
        else {
            val changedNumber = getInputNumber() - 1

            if (changedNumber >= 0) {
                binding.selectedPoints = numberFormat.format(changedNumber)
            }
        }
    }

    private fun checkThenCommitPoints() {
        getInputNumber().let {
            if (it > currentPoints) {
                Utility.toastShort(R.string.point_limit)
            }
            else {
                commitPoints(it)
            }
        }
    }

    private fun commitPoints(points: Int) {
        if (isFromPayment) {
            mainViewModel.updatePaymentPoints(points)
            popBack()
        }
        else if (points > 0) {
            phoneNumber?.let {
                showPointTransferConfirmationDialog(it, points)
            }
        }
    }

    private fun showPointTransferConfirmationDialog(phoneNumber: String, points: Int) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            titleRes = R.string.point_transfer,
            content = AppController.getAppContext().getString(R.string.confirm_point_transferring, points, phoneNumber),
            showCancelButton = true
        ) {
            transferPointAndPopBack(phoneNumber, points)
        }
    }

    private fun transferPointAndPopBack(phoneNumber: String, points: Int) {
        popBack()
        showLoadingDialog()
        mainViewModel.transferPoint(phoneNumber, points)
    }

    private fun getInputNumber(): Int {
        val input = binding.editPointInput.text.toString().takeIf { it.isNotEmpty() } ?: "0"

        return try {
            numberFormat.parse(input)?.toInt() ?: 0
        }
        catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }
}