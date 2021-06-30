package com.hsvibe.ui

import android.view.View
import androidx.activity.viewModels
import com.hsvibe.R
import com.hsvibe.databinding.ActivityLoginBinding
import com.hsvibe.model.Const
import com.hsvibe.model.UserToken
import com.hsvibe.model.UserTokenManager
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.login.UiLoginWebDialogFragment
import com.hsvibe.utilities.L
import com.hsvibe.viewmodel.LoginViewModel
import com.hsvibe.viewmodel.LoginViewModelFactory

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    private val loginViewModel by viewModels<LoginViewModel> { LoginViewModelFactory() }

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun getLoadingView(): View? = null

    override fun getContainerId(): Int? = null

    override fun init() {
        setButtonClick()
        observeLoginStatus()
    }

    private fun setButtonClick() {
        bindingView.buttonLogin.setOnClickListener(this)
        bindingView.buttonLater.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_login -> {
                openDialogFragment(UiLoginWebDialogFragment(), Const.BACK_LOGIN_DIALOG, true)
            }
            R.id.button_later -> {
                // TODO Go to Main
            }
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.liveUserToken.observe(this, { userToken ->
            L.i("on UserToken Changed!!!")
            dismissDialogFragment()
            //updateUserToken(userToken)
        })
    }

    private fun updateUserToken(userToken: UserToken) {
        UserTokenManager.setUserToken(userToken)
        // TODO Go to Main
    }
}