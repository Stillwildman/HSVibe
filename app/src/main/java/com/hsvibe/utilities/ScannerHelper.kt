package com.hsvibe.utilities

import com.budiyev.android.codescanner.*
import com.hsvibe.AppController

/**
 * Created by Vincent on 2021/9/9.
 */
object ScannerHelper {

    fun createScanner(scannerView: CodeScannerView,
                      decodeCallback: DecodeCallback? = null,
                      errorCallback: ErrorCallback? = null
    ): CodeScanner {
        return CodeScanner(AppController.getAppContext(), scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
            this.decodeCallback = decodeCallback
            this.errorCallback = errorCallback
        }
    }

}