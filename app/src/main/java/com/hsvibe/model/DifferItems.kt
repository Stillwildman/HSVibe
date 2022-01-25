package com.hsvibe.model

import androidx.recyclerview.widget.DiffUtil
import com.hsvibe.model.items.ItemAccountBonus
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon

/**
 * Created by Vincent on 2021/8/9.
 */
object DifferItems {

    object ContentItemDiffer : DiffUtil.ItemCallback<ItemContent.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemContent.ContentData, newItem: ItemContent.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: ItemContent.ContentData, newItem: ItemContent.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
                    && oldItem.title == newItem.title
                    && oldItem.content == newItem.content
                    && oldItem.share_url == newItem.share_url
        }
    }

    object CouponItemDiffer : DiffUtil.ItemCallback<ItemCoupon.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemCoupon.ContentData, newItem: ItemCoupon.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: ItemCoupon.ContentData, newItem: ItemCoupon.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
                    && oldItem.title == newItem.title
                    && oldItem.subtitle == newItem.subtitle
                    && oldItem.content == newItem.content
                    && oldItem.stock == newItem.stock
                    && oldItem.approval_at == newItem.approval_at
        }
    }

    object BonusItemDiffer : DiffUtil.ItemCallback<ItemAccountBonus.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemAccountBonus.ContentData, newItem: ItemAccountBonus.ContentData): Boolean {
            return oldItem.balance == newItem.balance && oldItem.point == newItem.point
        }

        override fun areContentsTheSame(oldItem: ItemAccountBonus.ContentData, newItem: ItemAccountBonus.ContentData): Boolean {
            return oldItem.balance == newItem.balance
                    && oldItem.point == newItem.point
                    && oldItem.operate == newItem.operate
                    && oldItem.note == newItem.note
                    && oldItem.created_at == newItem.created_at
                    && oldItem.updated_at == newItem.updated_at
        }
    }


}