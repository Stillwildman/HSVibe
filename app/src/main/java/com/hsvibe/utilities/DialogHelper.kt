package com.hsvibe.utilities

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.DialogLargeViewBinding
import com.hsvibe.databinding.DialogMemberTermsBinding
import com.hsvibe.databinding.DialogSmallViewBinding
import com.hsvibe.databinding.DialogSmallViewSingleButtonBinding
import com.hsvibe.utilities.Extensions.setOnSingleClickListener

/**
 * Created by Vincent on 2021/8/16.
 */
object DialogHelper {

    fun showLargeViewDialog(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes contentRes: Int,
        @StringRes positiveButtonRes: Int,
        @StringRes negativeButtonRes: Int = R.string.cancel,
        onButtonClick: (isPositive: Boolean) -> Unit
    ): AlertDialog {
        val bindingView = DataBindingUtil.inflate<DialogLargeViewBinding>(LayoutInflater.from(context), R.layout.dialog_large_view, null, false)

        val dialog = AlertDialog.Builder(context).apply {
            setView(bindingView.root)
            setCancelable(true)
        }.create()

        setupDialogWindowAttribute(dialog)

        bindingView.apply {
            textDialogTitle.text = AppController.getAppContext().getString(titleRes)
            textDialogContent.text = AppController.getAppContext().getString(contentRes)
            buttonPositive.text = AppController.getAppContext().getString(positiveButtonRes)
            buttonNegative.text = AppController.getAppContext().getString(negativeButtonRes)

            buttonNegative.setOnClickListener {
                dialog.dismiss()
            }
            buttonPositive.setOnSingleClickListener {
                dialog.dismiss()
                onButtonClick(true)
            }
        }
        return dialog.also { it.show() }
    }

    fun showSmallViewDialog(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes contentRes: Int,
        @StringRes positiveButtonRes: Int,
        @StringRes negativeButtonRes: Int = R.string.cancel,
        onButtonClick: (isPositive: Boolean) -> Unit
    ): AlertDialog {
        val bindingView = DataBindingUtil.inflate<DialogSmallViewBinding>(LayoutInflater.from(context), R.layout.dialog_small_view, null, false)

        val dialog = AlertDialog.Builder(context).apply {
            setView(bindingView.root)
            setCancelable(true)
        }.create()

        setupDialogWindowAttribute(dialog)

        bindingView.apply {
            textDialogTitle.text = AppController.getAppContext().getString(titleRes)
            textDialogContent.text = AppController.getAppContext().getString(contentRes)
            buttonPositive.text = AppController.getAppContext().getString(positiveButtonRes)
            buttonNegative.text = AppController.getAppContext().getString(negativeButtonRes)

            buttonNegative.setOnClickListener {
                dialog.dismiss()
            }
            buttonPositive.setOnSingleClickListener {
                dialog.dismiss()
                onButtonClick(true)
            }
        }
        return dialog.also { it.show() }
    }

    fun showSingleButtonDialog(
        context: Context,
        @StringRes titleRes: Int,
        @StringRes contentRes: Int,
        @StringRes buttonRes: Int = R.string.confirm
    ): AlertDialog {
        val bindingView = DataBindingUtil.inflate<DialogSmallViewSingleButtonBinding>(
            LayoutInflater.from(context), R.layout.dialog_small_view_single_button, null, false
        )

        val dialog = AlertDialog.Builder(context).apply {
            setView(bindingView.root)
            setCancelable(true)
        }.create()

        setupDialogWindowAttribute(dialog)

        bindingView.apply {
            textDialogTitle.text = AppController.getAppContext().getString(titleRes)
            textDialogContent.text = AppController.getAppContext().getString(contentRes)
            buttonConfirm.text = AppController.getAppContext().getString(buttonRes)

            buttonConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
        return dialog.also { it.show() }
    }

    fun showMemberTermsDialog(context: Context, onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
        val bindingView = DataBindingUtil.inflate<DialogMemberTermsBinding>(LayoutInflater.from(context), R.layout.dialog_member_terms, null, false)

        val dialog = AlertDialog.Builder(context, R.style.DialogAnimTheme).apply {
            setView(bindingView.root)
            setCancelable(true)
        }.create()

        bindingView.buttonTerms.setOnSingleClickListener {
            onTermsClick()
            dialog.dismiss()
        }
        bindingView.buttonPrivacy.setOnSingleClickListener {
            onPrivacyClick()
            dialog.dismiss()
        }
        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0.6f)
            show()
        }
    }

    private fun setupDialogWindowAttribute(dialog: AlertDialog) {
        dialog.apply {
            window?.run {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setOnShowListener {
                    val params: WindowManager.LayoutParams = attributes
                    params.width = (Utility.getScreenWidth() * 0.8).toInt()
                    attributes = params
                    setOnShowListener(null)
                }
            }
        }
    }
}