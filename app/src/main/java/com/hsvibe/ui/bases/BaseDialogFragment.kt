package com.hsvibe.ui.bases

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.utilities.L
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/06/27.
 */
abstract class BaseDialogFragment<BindingView : ViewDataBinding> : DialogFragment(R.layout.fragment_empty_container) {

    sealed class AnimType {
        object NoAnim : AnimType()
        object SlideUp : AnimType()
        object SlideFromRight : AnimType()
    }

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    protected abstract fun getLayoutId(): Int
    protected abstract fun getAnimType(): AnimType
    protected abstract fun canCanceledOnTouchOutside(): Boolean
    protected abstract fun setDialogWindowAttrs(window: Window)
    protected abstract fun init()
    protected abstract fun onDialogBackPressed(): Boolean

    protected lateinit var bindingView: BindingView

    private lateinit var fragmentCallback: FragmentContract.FragmentCallback

    protected var startTime: Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentCallback = context as FragmentContract.FragmentCallback
        }
        catch (e: ClassCastException) {
            e.printStackTrace()
            L.e(TAG, context.javaClass.simpleName + " must implement " + FragmentContract.FragmentCallback::class.java.simpleName)
        }
        startTime = System.nanoTime()
        L.d(TAG, "onAttach!!!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.d(TAG, "onCreate!!!")

        val styleRes = when (getAnimType()) {
            is AnimType.NoAnim -> R.style.FullScreenDialog
            is AnimType.SlideUp -> R.style.FullScreenDialog_SlideUpAnim
            is AnimType.SlideFromRight -> R.style.FullScreenDialog_SlideRightAnim
        }
        setStyle(STYLE_NORMAL, styleRes)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        L.d(TAG, "onCreateDialog!!!")

        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (onDialogBackPressed().not()) {
                    super.onBackPressed()
                }
            }
        }
        //val dialog = AlertDialog.Builder(requireContext(), R.style.DialogAnimTheme).create()

        dialog.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            setCanceledOnTouchOutside(canCanceledOnTouchOutside())
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        L.d(TAG, "onCreateView!!!")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d(TAG, "onViewCreated!!!")

        lifecycleScope.launch {
            val bindingViewDeferred = async {
                DataBindingUtil.inflate(LayoutInflater.from(view.context), getLayoutId(), view as ViewGroup?, false) as BindingView
            }
            bindingView = bindingViewDeferred.await()

            //view.setBackgroundColor(Color.TRANSPARENT)
            (view as ViewGroup?)?.addView(bindingView.root)
            init()
        }
    }

    override fun onStart() {
        super.onStart()
        L.d(TAG, "onStart!!!")
        setDialogSize()
    }

    private fun setDialogSize() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            attributes.run {
                dimAmount = 0.5f
                flags = flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            }
            attributes = attributes

            setDialogWindowAttrs(this)
        }
    }

    protected fun popBack(backName: String? = null) {
        fragmentCallback.onFragmentPopBack(backName)
    }

    protected fun showLoadingDialog() {
        fragmentCallback.showLoadingDialogFromFragment()
    }

    protected fun hideLoadingDialog() {
        fragmentCallback.hideLoadingDialogFromFragment()
    }

    override fun onResume() {
        super.onResume()
        L.d(TAG, "onResume!!!")
    }

    override fun onPause() {
        super.onPause()
        L.d(TAG, "onPause!!!")
    }

    override fun onStop() {
        super.onStop()
        L.d(TAG, "onStop!!!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        L.d(TAG, "onDestroyView!!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d(TAG, "onDestroy!!!")
    }

    override fun onDetach() {
        super.onDetach()
        L.d(TAG, "onDetach!!!")
    }
}