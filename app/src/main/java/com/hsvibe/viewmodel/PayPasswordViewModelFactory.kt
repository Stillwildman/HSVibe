package com.hsvibe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hsvibe.repositories.PayPasswordRepo

/**
 * Created by Vincent on 2022/5/9.
 */
class PayPasswordViewModelFactory(private val payPasswordRepo: PayPasswordRepo) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(PayPasswordViewModel::class.java) -> {
                    PayPasswordViewModel(payPasswordRepo)
                }
                else -> throw IllegalArgumentException("Unknown ViewModel (${modelClass.name}) class.")
            }
        } as T
    }

}