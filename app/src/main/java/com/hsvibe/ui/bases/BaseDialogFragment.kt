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
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/06/27.
 */
abstract class BaseDialogFragment<BindingView : ViewDataBinding> : DialogFragment() {

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    protected abstract fun getLayoutId(): Int
    protected abstract fun canCanceledOnTouchOutside(): Boolean
    protected abstract fun setDialogWindowAttrs(window: Window)
    protected abstract fun init()

    protected lateinit var bindingView: BindingView

    private lateinit var fragmentCallback: FragmentContract.FragmentCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentCallback = context as FragmentContract.FragmentCallback
        }
        catch (e: ClassCastException) {
            e.printStackTrace()
            L.e(TAG, context.javaClass.simpleName + " must implement " + FragmentContract.FragmentCallback::class.java.simpleName)
        }
        L.d(TAG, "onAttach!!!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.d(TAG, "onCreate!!!")
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        L.d(TAG, "onCreateDialog!!!")

        val dialog = super.onCreateDialog(savedInstanceState)
        //val dialog = AlertDialog.Builder(requireContext(), R.style.DialogAnimTheme).create()

        dialog.apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            setCanceledOnTouchOutside(canCanceledOnTouchOutside())
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        L.d(TAG, "onCreateView!!!")
        bindingView = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        init()
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d(TAG, "onViewCreated!!!")
        view.setBackgroundColor(Color.TRANSPARENT)
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

}