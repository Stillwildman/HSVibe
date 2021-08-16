package com.hsvibe.ui.bases

import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.model.Const
import com.hsvibe.tasks.TaskController
import com.hsvibe.ui.TabFragmentManager
import com.hsvibe.ui.fragments.UiBasicWebFragment
import com.hsvibe.ui.fragments.login.UiLoadingDialogFragment
import com.hsvibe.utilities.L
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Vincent on 2021/6/27.
 */
abstract class BaseFragmentActivity : BasePermissionActivity(),
    CoroutineScope,
    FragmentManager.OnBackStackChangedListener,
    FragmentContract.FragmentCallback {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + SupervisorJob()

    protected abstract fun getContainerId(): Int?

    private val fm : FragmentManager by lazy {
        supportFragmentManager.also { it.addOnBackStackChangedListener(this) }
    }

    protected val tabFragmentManager by lazy { TabFragmentManager(fm, lifecycleScope) }

    private val taskController by lazy { TaskController<Unit>() }

    private var fragmentJob: Job? = null

    private var activityCallback: FragmentContract.ActivityCallback? = null

    private var lastBackStackCount = 0

    override fun onBackStackChanged() {
        val backStackCount = fm.backStackEntryCount
        L.i("onBackStackChanged!!! Count: $backStackCount")

        if (backStackCount < lastBackStackCount) {
            resumeFragment()
        }
        lastBackStackCount = backStackCount
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

    protected fun openTabFragment(tabKey: String) {
        fragmentJob = launch {
            taskController.joinPreviousOrRun(TaskController.KEY_SWITCH_TAB_FRAGMENT) {
                getContainerId()?.let { tabFragmentManager.switchToTab(tabKey, it) }
            }
        }
    }

    protected fun showTabFragment(instance: Fragment) {
        fm.beginTransaction().show(instance).commit()
    }

    protected fun hideTabFragment(instance: Fragment) {
        fm.beginTransaction().hide(instance).commit()
    }

    protected fun openFragment(instance: Fragment, useReplace: Boolean, backName: String?) {
        fragmentJob = launch {
            taskController.joinPreviousOrRun(TaskController.KEY_OPEN_FRAGMENT) {
                getContainerId()?.let {
                    fm.findFragmentById(it)?.let { lastFragment ->
                        if (lastFragment == instance) return@joinPreviousOrRun
                    }

                    if (useReplace) {
                        fm.beginTransaction().replace(it, instance).addToBackStack(backName).commit()
                    } else {
                        fm.beginTransaction().add(it, instance).addToBackStack(backName).commit()
                    }
                }
            }
        }
    }

    private fun resumeFragment() {
        if (isFragmentNotEmpty()) {
            getContainerId()?.let {
                fm.getBackStackEntryAt(fm.backStackEntryCount - 1).name?.let { backName ->
                    fm.findFragmentById(it)?.apply {
                        L.d("onResume: ${javaClass.simpleName} BackName: $backName")
                        onResume()
                    }
                }
            }
        }
    }

    protected fun openDialogFragment(instance: DialogFragment, backName: String? = null, tag: String = Const.TAG_DIALOG_FRAGMENT) {
        fragmentJob = launch {
            taskController.joinPreviousOrRun(TaskController.KEY_OPEN_DIALOG_FRAGMENT) {
                fm.beginTransaction().let {
                    it.addToBackStack(backName ?: Const.BACK_COMMON_DIALOG)
                    instance.show(it, tag)
                }
            }
        }
    }

    protected fun openWebDialogFragment(url: String) {
        url.takeIf { it.isNotEmpty() }?.let { openDialogFragment(UiBasicWebFragment.newInstance(it), Const.BACK_WEB_VIEW_DIALOG) }
    }

    protected fun dismissDialogFragment(tag: String = Const.TAG_DIALOG_FRAGMENT) {
        fm.findFragmentByTag(tag)?.let {
            (it as DialogFragment).dismiss()
        }
    }

    protected fun showLoadingDialog() {
        UiLoadingDialogFragment().apply {
            isCancelable = false
            show(fm.beginTransaction(), Const.TAG_LOADING_DIALOG_FRAGMENT)
        }
    }

    protected fun hideLoadingDialog() {
        fm.findFragmentByTag(Const.TAG_LOADING_DIALOG_FRAGMENT)?.let {
            (it as DialogFragment).dismiss()
        }
    }

    override fun setupActivityCallback(activityCallback: FragmentContract.ActivityCallback?) {
        this.activityCallback = activityCallback
        L.i("setupActivityCallback!!!")
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

    override fun onFragmentOpenDialogFragment(instance: DialogFragment, backName: String?) {
        openDialogFragment(instance, backName)
    }

    override fun onFragmentOpenWebDialogFragment(url: String) {
        openWebDialogFragment(url)
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

    override fun onDestroy() {
        super.onDestroy()
        fragmentJob?.cancel()
    }
}