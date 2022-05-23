package com.hsvibe.ui.fragments.payment

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPointsSelectionBinding
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.Utility
import com.hsvibe.utilities.isNotNullOrEmpty
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.MainViewModel
import java.text.NumberFormat

/**
 * Created by Vincent on 2022/5/20.
 */
class UiPointSelectionFragment : BaseActionBarFragment<FragmentPointsSelectionBinding>() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_points_selection

    override fun getTitleRes(): Int = R.string.using_points

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var currentPoints = 0

    override fun onInitCompleted() {
        getCurrentPointsAndBind()
        setListeners()
    }

    private fun getCurrentPointsAndBind() {
        mainViewModel.liveCurrentBalance.observe(viewLifecycleOwner) {
            currentPoints = it.balance.toInt()
            binding.totalPoints = NumberFormat.getInstance().format(currentPoints)
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
                commitPoints()
            }
        }
    }

    private fun applyInputNumber(number: String) {
        if (number.isNotNullOrEmpty()) {
            binding.selectedPoints = number.toInt()
        }
        else {
            binding.editPointInput.text.clear()
        }
    }

    private fun changeInputNumber(isPlus: Boolean) {
        if (isPlus) {
            val changedNumber = getInputNumber() + 10
            if (changedNumber <= currentPoints) {
                binding.selectedPoints = changedNumber
            }
            else {
                Utility.toastShort(R.string.point_limit)
            }
        }
        else {
            val changedNumber = getInputNumber() - 10

            if (changedNumber >= 0) {
                binding.selectedPoints = changedNumber
            }
        }
    }

    private fun commitPoints() {
        getInputNumber().let {
            if (it > currentPoints) {
                Utility.toastShort(R.string.point_limit)
            }
            else {
                mainViewModel.updatePaymentPoints(it)
                popBack()
            }
        }
    }

    private fun getInputNumber(): Int {
        return binding.editPointInput.text.toString().takeIf { it.isNotNullOrEmpty() }?.toInt() ?: 0
    }
}