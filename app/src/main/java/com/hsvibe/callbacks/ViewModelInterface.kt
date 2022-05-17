package com.hsvibe.callbacks

import androidx.lifecycle.ViewModel

/**
 * Created by Vincent on 2022/4/19.
 */
interface ViewModelInterface<ViewModelType : ViewModel> {

    val viewModel: ViewModelType

}