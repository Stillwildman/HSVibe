package com.hsvibe.ui.fragments.home

import androidx.fragment.app.activityViewModels
import com.hsvibe.R
import com.hsvibe.databinding.FragmentHomeBinding
import com.hsvibe.repositories.UserRepoImpl
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.MainViewModelFactory

/**
 * Created by Vincent on 2021/7/4.
 */
class UiHomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mainViewModel by activityViewModels<MainViewModel> { MainViewModelFactory(UserRepoImpl()) }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun init() {

    }

    override fun onBackButtonPressed(): Boolean = false

}