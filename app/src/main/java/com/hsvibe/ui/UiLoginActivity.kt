package com.hsvibe.ui

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.hsvibe.R
import com.hsvibe.databinding.ActivityLoginBinding
import com.hsvibe.model.Const
import com.hsvibe.model.UserInfoManager
import com.hsvibe.model.UserToken
import com.hsvibe.ui.bases.BaseActivity
import com.hsvibe.ui.fragments.login.UiLoginWebDialogFragment
import com.hsvibe.utilities.ContextExt.startActivitySafely
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
                openDialogFragment(UiLoginWebDialogFragment(), Const.BACK_LOGIN_DIALOG)
            }
            R.id.button_later -> {
                goToMain()
            }
        }
    }

    private fun observeLoginStatus() {
        loginViewModel.liveUserToken.observe(this, { userToken ->
            dismissDialogFragment()
            updateUserToken(userToken)
        })
    }

    private fun updateUserToken(userToken: UserToken) {
        UserInfoManager.setUserToken(userToken)
        goToMain()
    }

    private fun goToMain() {
        this.startActivitySafely(Intent(this, UiMainActivity::class.java))
        this.finish()
    }
}