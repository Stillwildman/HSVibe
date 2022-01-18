package com.hsvibe.viewadapters

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.ui.adapters.MyBaseAdapter

/**
 * Created by Vincent on 2021/8/15.
 */
@BindingAdapter(value = ["pairList", "hasHintHeader"])
fun setupSpinnerByPairList(spinner: Spinner, pairList: MutableList<Pair<String, String?>>?, hasHintHeader: Boolean) {
    pairList?.let {
        if (hasHintHeader) {
            it.add(0, Pair(AppController.getString(R.string.please_select), null))
        }

        if (spinner.adapter == null || spinner.adapter.count == 0) {
            spinner.adapter = MyBaseAdapter(it, hasHintHeader)
        }
        else {
            (spinner.adapter as MyBaseAdapter).updateList(it)
        }
    }
}

@BindingAdapter(value = ["largePairList"])
fun setupSpinnerByPairList(spinner: Spinner, pairList: MutableList<Pair<String, String?>>?) {
    pairList?.let {
        if (spinner.adapter == null || spinner.adapter.count == 0) {
            spinner.adapter = MyBaseAdapter(it, hasHintHeader = false, useLargeLayout = true)
        }
        else {
            (spinner.adapter as MyBaseAdapter).updateList(it)
        }
    }
}