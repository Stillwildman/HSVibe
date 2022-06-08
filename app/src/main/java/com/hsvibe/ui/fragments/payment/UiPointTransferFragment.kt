package com.hsvibe.ui.fragments.payment

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPointTransferBinding
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2022/6/8.
 */
class UiPointTransferFragment : BaseActionBarFragment<FragmentPointTransferBinding>() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_point_transfer

    override fun getTitleRes(): Int? = null

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onInitCompleted() {
        setListeners()
    }

    private fun setListeners() {
        binding.editEnterPhoneNumber.addTextChangedListener {
            it?.let { checkNumberValidation(it.toString()) }
        }

        binding.buttonTransferPoint.setOnSingleClickListener {
            openPointSelectionPage()
        }
    }

    private fun checkNumberValidation(number: String) {
        binding.isValid = number.length == 10
                && number.startsWith("09")
                && number != mainViewModel.liveUserInfo.value?.getMobileNumber()
    }

    private fun openPointSelectionPage() {
        openDialogFragment(UiPointSelectionFragment.newInstance(binding.editEnterPhoneNumber.text.toString()))
        observePointTransferring()
    }

    private fun observePointTransferring() {
        mainViewModel.liveMessage.observe(viewLifecycleOwner) {
            hideLoadingDialog()

            if (it.isSuccess()) {
                showSuccessDialog()
            }
            else {
                showFailDialog(it.message)
            }
        }
    }

    private fun showSuccessDialog() {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            titleRes = R.string.point_transfer,
            content = AppController.getString(R.string.point_transfer_success),
            iconRes = R.drawable.ic_check_white
        ) {
            popBack()
        }
    }

    private fun showFailDialog(message: String) {
        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            theme = R.style.DialogSurfaceCaution,
            titleRes = R.string.point_transfer,
            content = message,
            iconRes = R.drawable.ic_close_white
        ) {
            popBack()
        }
    }
}