package com.hsvibe.viewadapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.*

/**
 * Created by Vincent on 2022/4/19.
 */
object BarcodeBinding {

    private val scope by lazy {
        CoroutineScope(Dispatchers.Main + CoroutineName("BarcodeBinding") + SupervisorJob())
    }

    @JvmStatic
    @BindingAdapter("createBarcode")
    fun createBarcode(imageView: ImageView, code: String?) {
        code?.let {
            imageView.visibility = View.VISIBLE
            try {
                scope.launch {
                    val barcodeBitmapDeferred = async {
                        val bitMatrix = MultiFormatWriter().encode(it, BarcodeFormat.CODE_128, imageView.measuredWidth, imageView.measuredHeight)
                        val barcodeEncoder = BarcodeEncoder()
                        barcodeEncoder.createBitmap(bitMatrix)
                    }
                    val barcodeBitmap = barcodeBitmapDeferred.await()
                    imageView.setImageBitmap(barcodeBitmap)
                }
            }
            catch (e: WriterException) {
                e.printStackTrace()
            }
        }
    }

}