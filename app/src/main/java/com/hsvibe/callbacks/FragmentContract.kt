package com.hsvibe.callbacks

/**
 * Created by Vincent on 2021/6/28.
 */
class FragmentContract {

    interface FragmentCallback {
        fun setupActivityCallback(activityCallback: ActivityCallback)
        fun onFragmentPopBack(backName: String?)
        fun showLoadingDialogFromFragment()
        fun hideLoadingDialogFromFragment()
    }

    interface ActivityCallback {
        fun onBackButtonPressed(): Boolean
    }

}