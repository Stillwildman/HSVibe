package com.hsvibe.ui.bases

import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.model.Const
import com.hsvibe.ui.fragments.login.UiLoadingDialogFragment
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/6/27.
 */
abstract class BaseFragmentActivity : BasePermissionActivity(), FragmentManager.OnBackStackChangedListener, FragmentContract.FragmentCallback {

    protected abstract fun getContainerId(): Int?

    private val fm : FragmentManager by lazy {
        supportFragmentManager.also { it.addOnBackStackChangedListener(this) }
    }

    private var activityCallback: FragmentContract.ActivityCallback? = null

    private var backStackLastCount = 0

    override fun onBackStackChanged() {
        val backStackCount = fm.backStackEntryCount
        L.i("onBackStackChanged!!! Count: $backStackCount")

        if (backStackCount < backStackLastCount) {
            resumeFragment()
        }
        backStackLastCount = backStackCount
    }

    private fun isFragmentNotEmpty(): Boolean = fm.backStackEntryCount > 0

    protected fun isFragmentsMoreThanOne(): Boolean = fm.backStackEntryCount > 1

    protected fun popBack(@Nullable backName : String? = null) {
        if (isFragmentNotEmpty()) {
            if (backName == null) {
                fm.popBackStackImmediate()
            }
            else {
                fm.popBackStackImmediate(backName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    protected fun openFragment(instance: Fragment, useReplace: Boolean, backName: String?) {
        getContainerId()?.let {
            fm.findFragmentById(it)?.let { lastFragment ->
                if (lastFragment == instance) return
            }

            if (useReplace) {
                fm.beginTransaction().replace(it, instance).addToBackStack(backName).commit()
            }
            else {
                fm.beginTransaction().add(it, instance).addToBackStack(backName).commit()
            }
        }
    }

    private fun resumeFragment() {
        if (isFragmentNotEmpty()) {
            getContainerId()?.let {
                fm.getBackStackEntryAt(fm.backStackEntryCount - 1).name?.let { backName ->
                    fm.findFragmentById(it)?.apply {
                        L.d("onResume: ${javaClass.name} BackName: $backName")
                        onResume()
                    }
                }
            }
        }
    }

    protected fun openDialogFragment(instance: DialogFragment, backName: String?, useSlideUpAnim: Boolean = false) {
        fm.beginTransaction().let {
            if (useSlideUpAnim) {
                it.setCustomAnimations(0, 0, R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom)
            }
            it.addToBackStack(backName ?: Const.BACK_COMMON_DIALOG)
            instance.show(it, Const.DIALOG_FRAGMENT)
        }
    }

    protected fun dismissDialogFragment() {
        fm.findFragmentByTag(Const.DIALOG_FRAGMENT)?.let {
            (it as DialogFragment).dismiss()
        }
    }

    protected fun showLoadingDialog() {
        UiLoadingDialogFragment().apply {
            isCancelable = false
            show(fm.beginTransaction(), Const.LOADING_DIALOG_FRAGMENT)
        }
    }

    protected fun hideLoadingDialog() {
        fm.findFragmentByTag(Const.LOADING_DIALOG_FRAGMENT)?.let {
            (it as DialogFragment).dismiss()
        }
    }

    override fun setupActivityCallback(activityCallback: FragmentContract.ActivityCallback) {
        this.activityCallback = activityCallback
    }

    override fun onFragmentPopBack(backName: String?) {
        popBack(backName)
    }

    override fun showLoadingDialogFromFragment() {
        showLoadingDialog()
    }

    override fun hideLoadingDialogFromFragment() {
        hideLoadingDialog()
    }

    override fun onBackPressed() {
        if (isFragmentNotEmpty()) {
            activityCallback?.let {
                if (!it.onBackButtonPressed()) popBack()
            } ?: super.onBackPressed()
        }
        else {
            super.onBackPressed()
        }
    }
}