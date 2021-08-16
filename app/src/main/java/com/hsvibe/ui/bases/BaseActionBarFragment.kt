package com.hsvibe.ui.bases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.InflateEmptyActionBarFragmentBinding
import com.hsvibe.model.LoadingStatus
import com.hsvibe.utilities.Extensions.setOnSingleClickListener
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/8/4.
 */
abstract class BaseActionBarFragment<BindingView : ViewDataBinding> : BaseDialogFragment<InflateEmptyActionBarFragmentBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_empty_action_bar_fragment

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {}

    abstract fun getFragmentLayoutId(): Int

    abstract fun getTitleRes(): Int?

    abstract fun onInitCompleted()

    protected lateinit var binding: BindingView

    override fun init() {
        initViews()
    }

    private fun initViews() {
        viewLifecycleOwner.lifecycleScope.launch {
            val headerInitDeferred = async {
                initHeader()
            }
            headerInitDeferred.await()

            bindingView.layoutContainer.let { container ->
                val bindingDeferred = async {
                    DataBindingUtil.inflate(LayoutInflater.from(context), getFragmentLayoutId(), container as ViewGroup?, false) as BindingView
                }
                binding = bindingDeferred.await()

                container.addView(binding.root)
            }
            onInitCompleted()
        }
    }

    private fun initHeader() {
        bindingView.apply {
            getTitle()?.let {
                textHeaderTitle.text = it
            } ?: run {
                getTitleRes()?.let { textHeaderTitle.setText(it) }
            }

            getActionBarBackgroundColor()?.let { layoutActionBar.setBackgroundColor(it) }

            val backButtonRes = when (getAnimType()) {
                is AnimType.NoAnim,
                is AnimType.SlideFromRight -> R.drawable.selector_back
                is AnimType.SlideUp -> R.drawable.selector_close
            }

            buttonBack.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), backButtonRes))
            buttonBack.setOnClickListener {
                onDialogBackPressed()
            }
            getMenuOptionIconRes()?.let {
                buttonMenuOption.visibility = View.VISIBLE
                buttonMenuOption.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), it))
                buttonMenuOption.setOnSingleClickListener { onMenuOptionClick() }
            }
        }
    }

    protected open fun getTitle(): String? = null

    protected fun setTitle(title: String) {
        bindingView.textHeaderTitle.text = title
    }

    @ColorInt
    protected open fun getActionBarBackgroundColor(): Int? = null

    protected open fun getMenuOptionIconRes(): Int? = null

    protected open fun onMenuOptionClick() {

    }

    protected fun showLoadingCircle() {
        bindingView.loadingCircle.visibility = View.VISIBLE
    }

    protected fun hideLoadingCircle() {
        bindingView.loadingCircle.visibility = View.GONE
    }

    protected fun handleLoadingStatus(loadingStatus: LoadingStatus) {
        when (loadingStatus) {
            is LoadingStatus.OnLoadingStart -> showLoadingCircle()
            is LoadingStatus.OnLoadingEnd -> hideLoadingCircle()
            is LoadingStatus.OnError -> Utility.toastLong(loadingStatus.errorMessage)
        }
    }

    override fun onDialogBackPressed(): Boolean {
        popBack()
        return true
    }
}