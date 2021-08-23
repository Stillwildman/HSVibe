package com.hsvibe.ui.fragments.member

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentMemberInfoBinding
import com.hsvibe.model.Const
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.repositories.ProfileRepoImpl
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.adapters.MyBaseAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.Extensions.getContextSafely
import com.hsvibe.utilities.Extensions.getPairSecondValue
import com.hsvibe.utilities.Extensions.setOnSingleClickListener
import com.hsvibe.utilities.Utility
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory
import com.hsvibe.viewmodel.ProfileViewModel
import com.hsvibe.viewmodel.ProfileViewModelFactory
import java.util.*

/**
 * Created by Vincent on 2021/8/15.
 */
class UiMemberInfoFragment private constructor() : BaseActionBarFragment<FragmentMemberInfoBinding>() {

    companion object {
        fun newInstance(useSlideUpAnim: Boolean): UiMemberInfoFragment {
            return UiMemberInfoFragment().apply {
                arguments = Bundle().also { it.putBoolean(Const.BUNDLE_USE_SLIDE_UP_ANIM, useSlideUpAnim) }
            }
        }
    }

    private val mainViewModel by activityViewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }

    private val profileViewModel by viewModels<ProfileViewModel> {
        ProfileViewModelFactory(ProfileRepoImpl(mainViewModel.liveUserInfo.value))
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_member_info

    override fun getTitleRes(): Int = R.string.member_info

    override fun getAnimType(): AnimType {
        return if (arguments?.getBoolean(Const.BUNDLE_USE_SLIDE_UP_ANIM) == true)
            AnimType.SlideUp
        else
            AnimType.SlideFromRight
    }

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    override fun onInitCompleted() {
        setupBinding()
        startObserveLoadingStatus()
        initSpinnerData()
        setCitySpinnerListener()
        setClickListeners()
    }

    private fun setupBinding() {
        binding.profileViewModel = profileViewModel
        binding.lifecycleOwner = this
    }

    private fun startObserveLoadingStatus() {
        profileViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun initSpinnerData() {
        profileViewModel.setupPostUserInfo()
        profileViewModel.setupGenderList()
        profileViewModel.setupDistrictsList()
    }

    private fun setCitySpinnerListener() {
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    updatePostalSpinnerData(profileViewModel.postalPairList[position - 1])
                } else {
                    clearPostalSpinnerData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updatePostalSpinnerData(pairList: List<Pair<String, String?>>) {
        binding.spinnerPostal.run {
            adapter?.let {
                (it as? MyBaseAdapter)?.updateList(pairList)
            } ?: run {
                adapter = MyBaseAdapter(pairList.toMutableList(), false)
            }
        }
    }

    private fun clearPostalSpinnerData() {
        (binding.spinnerPostal.adapter as? MyBaseAdapter)?.clearList()
    }

    private fun setClickListeners() {
        binding.textBirthday.setOnSingleClickListener {
            showDataPickerDialog()
        }

        binding.buttonDone.setOnSingleClickListener { view ->
            AppController.instance.hideKeyboard(view)
            updatePostUserInfoData()
        }
    }

    private fun showDataPickerDialog() {
        val date = binding.textBirthday.text.takeIf { it.isNotEmpty() }?.let {
            Utility.convertDateStringToDate(it.toString())
        }
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }

        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(getContextSafely(), { _, year, month, dayOfMonth ->
            binding.textBirthday.text = AppController.getAppContext().getString(R.string.birthday_format, year, month + 1, dayOfMonth)
        }, currentYear, currentMonth, currentDay
        ).show()
    }

    private fun updatePostUserInfoData() {
        profileViewModel.livePostUserInfo.value?.apply {
            if (isAllInputValidated(this)) {
                gender = binding.spinnerGender.getPairSecondValue()
                //binding.spinnerPostal.getPairSecondValue() // TODO Update regions?

                mainViewModel.updateUserInfo(this) { isSuccess ->
                    if (isSuccess) {
                        Utility.toastShort(R.string.update_success)
                        popBack()
                    } else {
                        Utility.toastShort(R.string.update_failed)
                    }
                }
            }
        }
    }

    private fun isAllInputValidated(postUserInfo: PostUpdateUserInfo): Boolean {
        return postUserInfo.let {
            when {
                it.first_name.isNullOrEmpty() or
                it.last_name.isNullOrEmpty() -> {
                    showNameRequireDialog()
                    false
                }
                else -> true
            }
        }
    }

    private fun showNameRequireDialog() {
        DialogHelper.showSingleButtonDialog(getContextSafely(), R.string.data_error, R.string.name_is_necessary)
    }
}