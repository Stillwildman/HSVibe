package com.hsvibe.callbacks

/**
 * Created by Vincent on 2021/8/12.
 */
interface OnAnyItemClickCallback<Item : Any> {

    fun onItemClick(item: Item)

}