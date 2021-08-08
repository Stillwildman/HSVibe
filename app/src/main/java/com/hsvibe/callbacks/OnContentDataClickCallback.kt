package com.hsvibe.callbacks

import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemContent

/**
 * Created by Vincent on 2021/8/5.
 */
interface OnContentDataClickCallback {

    fun onContentDataClick(contentItem: ItemContent.ContentData, position: Int = Const.NO_POSITION)

}