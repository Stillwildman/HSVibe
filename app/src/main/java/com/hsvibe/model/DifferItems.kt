package com.hsvibe.model

import androidx.recyclerview.widget.DiffUtil
import com.hsvibe.model.items.*

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

    object MyCouponItemDiffer : DiffUtil.ItemCallback<ItemMyCoupon.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemMyCoupon.ContentData, newItem: ItemMyCoupon.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: ItemMyCoupon.ContentData, newItem: ItemMyCoupon.ContentData): Boolean {
            return oldItem.uuid == newItem.uuid
                    && oldItem.title == newItem.title
                    && oldItem.subtitle == newItem.subtitle
                    && oldItem.content == newItem.content
                    && oldItem.expire_at == newItem.expire_at
        }
    }

    object CardDetailDiffer : DiffUtil.ItemCallback<ItemCardList.CardData.CardDetail>() {
        override fun areItemsTheSame(oldItem: ItemCardList.CardData.CardDetail, newItem: ItemCardList.CardData.CardDetail): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ItemCardList.CardData.CardDetail, newItem: ItemCardList.CardData.CardDetail): Boolean {
            return oldItem.key == newItem.key
                    && oldItem.display == newItem.display
                    && oldItem.expire_date == newItem.expire_date
                    && oldItem.bank_number == newItem.bank_number
        }
    }

    object TransactionDiffer : DiffUtil.ItemCallback<ItemTransactions.ContentData>() {
        override fun areItemsTheSame(oldItem: ItemTransactions.ContentData, newItem: ItemTransactions.ContentData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemTransactions.ContentData, newItem: ItemTransactions.ContentData): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.transaction_code == newItem.transaction_code
                    && oldItem.store_name == newItem.store_name
                    && oldItem.action == newItem.action
                    && oldItem.created_at == newItem.created_at
        }

    }
}