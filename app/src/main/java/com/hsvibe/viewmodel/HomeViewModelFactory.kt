package com.hsvibe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hsvibe.repositories.HomeContentRepo

/**
 * Created by Vincent on 2021/7/19.
 */
class HomeViewModelFactory(private val homeContentRepo: HomeContentRepo, private val mainViewModel: MainViewModel) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(homeContentRepo, mainViewModel)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class.")
            }
        } as T
    }
}