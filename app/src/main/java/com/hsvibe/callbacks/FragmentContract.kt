package com.hsvibe.callbacks

import androidx.fragment.app.DialogFragment

/**
 * Created by Vincent on 2021/6/28.
 */
class FragmentContract {

    interface FragmentCallback {
        fun setupActivityCallback(activityCallback: ActivityCallback?)
        fun onFragmentPopBack(backName: String?)
        fun showLoadingDialogFromFragment()
        fun hideLoadingDialogFromFragment()
        fun onFragmentOpenDialogFragment(instance: DialogFragment, backName: String?)
        fun onFragmentOpenWebDialogFragment(url: String)
    }

    interface ActivityCallback {
        /**
         * @return Return true to consume the Back pressed event.
         *
         * false to call Fragment's popBack method.
         */
        fun onBackButtonPressed(): Boolean
    }

}