package com.hsvibe.utilities

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.*

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
            textDialogTitle.text = AppController.getString(titleRes)
            textDialogContent.text = AppController.getString(contentRes)
            buttonPositive.text = AppController.getString(positiveButtonRes)
            buttonNegative.text = AppController.getString(negativeButtonRes)

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
        title: String,
        content: String,
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
            textDialogTitle.text = title
            textDialogContent.text = content
            buttonPositive.text = AppController.getString(positiveButtonRes)
            buttonNegative.text = AppController.getString(negativeButtonRes)

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
        return showSmallViewDialog(
            context,
            AppController.getString(titleRes),
            AppController.getString(contentRes),
            positiveButtonRes,
            negativeButtonRes,
            onButtonClick
        )
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
            textDialogTitle.text = AppController.getString(titleRes)
            textDialogContent.text = AppController.getString(contentRes)
            buttonConfirm.text = AppController.getString(buttonRes)

            buttonConfirm.setOnClickListener {
                dialog.dismiss()
            }
        }
        return dialog.also { it.show() }
    }

    fun showMemberTermsDialog(context: Context, onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
        val bindingView = DataBindingUtil.inflate<DialogMemberTermsBinding>(LayoutInflater.from(context), R.layout.dialog_member_terms, null, false)

        val dialog = AlertDialog.Builder(context, R.style.DialogFasterAnimTheme).apply {
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

    fun showSimpleHsVibeDialog(
        context: Context,
        @StringRes titleRes: Int,
        content: String,
        @DrawableRes iconRes: Int,
        @StringRes positiveButtonRes: Int,
        onButtonClick: () -> Unit
    ): AlertDialog {
        val bindingView = DataBindingUtil.inflate<DialogSimpleHsvibeViewBinding>(LayoutInflater.from(context), R.layout.dialog_simple_hsvibe_view, null, false)

        val dialog = AlertDialog.Builder(context).apply {
            setView(bindingView.root)
            setCancelable(true)
        }.create()

        bindingView.apply {
            textDialogTitle.text = AppController.getString(titleRes)
            textDialogContent.text = content
            imageDialogStatus.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), iconRes))
            buttonConfirm.text = AppController.getString(positiveButtonRes)

            buttonConfirm.setOnSingleClickListener {
                dialog.dismiss()
                onButtonClick()
            }
        }
        return dialog.also {
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.window?.setDimAmount(0.6f)
            it.show()
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