package com.hsvibe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hsvibe.repositories.ProfileRepo

/**
 * Created by Vincent on 2021/08/15.
 */
class ProfileViewModelFactory(private val profileRepo: ProfileRepo) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(ProfileViewModel::class.java) -> {
                    ProfileViewModel(profileRepo)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class.")
            }
        } as T
    }
}