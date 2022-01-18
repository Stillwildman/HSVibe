package com.hsvibe.ui

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.hsvibe.R
import com.hsvibe.databinding.ActivityLoginBinding
import com.hsvibe.model.Const
import com.hsvibe.model.UserToken
import com.hsvibe.model.UserTokenManager
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.login.UiLoginWebDialogFragment
import com.hsvibe.utilities.ContextExt.startActivitySafelyAndFinish
import com.hsvibe.utilities.setOnSingleClickListener
import com.hsvibe.viewmodel.LoginViewModel

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun getLoadingView(): View? = null

    override fun getContainerId(): Int? = null

    override fun init() {
        setButtonClick()
        observeLoginStatus()
    }

    private fun setButtonClick() {
        bindingView.buttonLogin.setOnSingleClickListener {
            openDialogFragment(UiLoginWebDialogFragment(), Const.BACK_LOGIN_DIALOG)
        }

        bindingView.buttonLater.setOnSingleClickListener {
            goToMain()
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.liveUserToken.observe(this, { userToken ->
            dismissDialogFragment()
            updateUserToken(userToken)
        })
    }

    private fun updateUserToken(userToken: UserToken) {
        UserTokenManager.setUserToken(userToken)
        goToMain()
    }

    private fun goToMain() {
        this.startActivitySafelyAndFinish(Intent(this, UiMainActivity::class.java))
    }
}