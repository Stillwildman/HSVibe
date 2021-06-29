package com.hsvibe.callbacks

/**
 * Created by Vincent on 2021/6/28.
 */
class FragmentContract {

    interface FragmentCallback {
        fun onFragmentPopBack(backName: String?)
    }

    interface ActivityCallback {
        fun onBackButtonPressed(): Boolean
    }

}