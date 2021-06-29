package com.hsvibe.callbacks

/**
 * Created by Vincent on 2021/06/28.
 */
interface OnDataGetCallback<Item> {

    fun onDataGet(item: Item?)

    fun onDataGetFailed(errorMessage: String?)

}