package com.hsvibe.ui.fragments.member

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPasswordModificationBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.Utility
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2022/4/30.
 */
class UiPasswordModifyFragment : BaseActionBarFragment<FragmentPasswordModificationBinding>() {

    companion object {
        private const val PASSWORD_MIN_LENGTH = 6
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_password_modification

    override fun getTitleRes(): Int = R.string.change_password

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    private var password1: String = Const.EMPTY_STRING
    private var password2: String = Const.EMPTY_STRING

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onInitCompleted() {
        setClickListener()
        startObserveLoadingStatus()
        setTextWatcher()
    }

    private fun setClickListener() {
        binding.buttonConfirm.setOnSingleClickListener {
            updateUserPassword()
        }
    }

    private fun startObserveLoadingStatus() {
        mainViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun setTextWatcher() {
        binding.editPassword.addTextChangedListener {
            it?.let { password1 = it.toString() }
            checkIsPasswordValid()
        }

        binding.editConfirmPassword.addTextChangedListener {
            it?.let { password2 = it.toString() }
            checkIsPasswordValid()
        }
    }

    private fun checkIsPasswordValid() {
        val isPasswordValid = password1.length >= PASSWORD_MIN_LENGTH && password1 == password2
        binding.isValid = isPasswordValid
    }

    private fun updateUserPassword() {
        mainViewModel.updatePassword(binding.editPassword.text.toString()) { isSuccess ->
            if (isSuccess) {
                Utility.toastShort(R.string.update_success)
                popBack()
            } else {
                Utility.toastShort(R.string.update_failed)
            }
        }
    }

    override fun showLoadingCircle() {
        super.showLoadingCircle()
        binding.isValid = false
    }

    override fun hideLoadingCircle() {
        super.hideLoadingCircle()
        checkIsPasswordValid()
    }
}