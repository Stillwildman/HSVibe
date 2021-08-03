package com.hsvibe.ui.bases

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.InflateEmptyActionBarFragmentBinding
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

    abstract fun getTitleRes(): Int

    abstract fun onInitCompleted()

    protected lateinit var binding: BindingView

    override fun init() {
        initViews()
    }

    private fun initViews() {
        lifecycleScope.launch {
            initHeader()

            with(bindingView.layoutContainer) {
                val bindingDeferred = async {
                    DataBindingUtil.inflate(LayoutInflater.from(context), getFragmentLayoutId(), this as ViewGroup?, false) as BindingView
                }
                val binding = bindingDeferred.await()

                addView(binding.root)

                getBackgroundColorRes()?.let { setBackgroundColor(ContextCompat.getColor(AppController.getAppContext(), it)) }
            }
            onInitCompleted()
        }
    }

    private fun initHeader() {
        bindingView.apply {
            getTitle()?.let {
                textHeaderTitle.text = it
            } ?: run {
                textHeaderTitle.setText(getTitleRes())
            }

            val backButtonRes = when (getAnimType()) {
                is AnimType.NoAnim,
                is AnimType.SlideFromRight -> R.drawable.ic_back_arrow_light
                is AnimType.SlideUp -> R.drawable.ic_close_light
            }

            buttonBack.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), backButtonRes))
            buttonBack.setOnClickListener {
                onDialogBackPressed()
            }

            getMenuOptionIconRes()?.let {
                buttonMenuOption.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), it))
                buttonMenuOption.setOnClickListener {
                    onMenuOptionClick()
                }
            }
        }
    }

    protected fun getTitle(): String? = null

    protected fun getBackgroundColorRes(): Int? = null

    protected fun getMenuOptionIconRes(): Int? = null

    protected fun onMenuOptionClick() {

    }

    override fun onDialogBackPressed(): Boolean {
        popBack()
        return true
    }
}