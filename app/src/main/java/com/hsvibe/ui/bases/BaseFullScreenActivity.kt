package com.hsvibe.ui.bases

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.hsvibe.R

/**
 * Created by Vincent on 2021/6/25.
 */
abstract class BaseFullScreenActivity : BasePermissionActivity() {

    protected abstract fun getLayoutId(): Int
    protected abstract fun init()

    protected val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setTheme(R.style.AppFullScreenTheme)
        setFullScreenFlags()
        setContentView(getLayoutId())
        init()
    }

    private fun setFullScreenFlags() {
        window.run {
            setDecorFitsSystemWindows(false)
            insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        /*
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        */
    }
}