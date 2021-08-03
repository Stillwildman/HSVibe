package com.hsvibe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hsvibe.repositories.ContentRepo

/**
 * Created by Vincent on 2021/7/19.
 */
class HomeViewModelFactory(private val contentRepo: ContentRepo, private val mainViewModel: MainViewModel) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(contentRepo, mainViewModel)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class.")
            }
        } as T
    }
}