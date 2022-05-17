package com.hsvibe.viewadapters

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
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

    @JvmStatic
    @BindingAdapter("createQRCode")
    fun createQRCode(imageView: ImageView, code: String?) {
        code?.let {
            imageView.visibility = View.VISIBLE
            try {
                scope.launch {
                    val qrcodeBitmapDeferred = async {
                        val bitMatrix = QRCodeWriter().encode(it, BarcodeFormat.QR_CODE, imageView.measuredWidth, imageView.measuredHeight)
                        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.ARGB_8888)

                        for (x in 0 until bitMatrix.width) {
                            for (y in 0 until bitMatrix.height) {
                                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.TRANSPARENT)
                            }
                        }
                        bitmap
                    }
                    val barcodeBitmap = qrcodeBitmapDeferred.await()
                    imageView.setImageBitmap(barcodeBitmap)
                }
            }
            catch (e: WriterException) {
                e.printStackTrace()
            }
        }
    }
}