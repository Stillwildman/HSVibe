package com.hsvibe.ui.fragments.member

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPointHistoryBinding
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2021/8/23.
 */
class UiPointHistoryFragment : BaseActionBarFragment<FragmentPointHistoryBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_point_history

    override fun getTitleRes(): Int = R.string.point_record

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    override fun onInitCompleted() {
        bindPointsRecord()
    }

    private fun bindPointsRecord() {
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
    }


}