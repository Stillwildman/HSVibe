package com.hsvibe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hsvibe.repositories.UserRepo

/**
 * Created by Vincent on 2021/6/29.
 */
class MainViewModelFactory(private val userRepo: UserRepo) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(userRepo)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class.")
            }
        } as T
    }
}