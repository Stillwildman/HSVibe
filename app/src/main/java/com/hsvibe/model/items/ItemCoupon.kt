package com.hsvibe.model.items

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.utilities.isNotNullOrEmpty
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat

/**
 * Created by Vincent on 2021/7/20.
 */
data class ItemCoupon(
    @SerializedName("data")
    val contentData: List<ContentData>,
    val meta: Meta
) {
    @Parcelize
    data class ContentData(
        val uuid: String,
        val title: String,
        val subtitle: String,
        val brief: String,
        val content: String,
        val amount: Int,
        val point: Int,
        val published: Int,
        val stock: Int,
        val expire_at: String,
        val approval_at: String,
        val brand_thumb: String,
        val brand_name: String,
        val media: Media,
        val stores: Stores
    ) : Parcelable {
        @Parcelize
        data class Media(
            @SerializedName("data")
            val mediaData: List<MediaData>
        ) : Parcelable

        @Parcelize
        data class Stores(
            @SerializedName("data")
            val storeData: List<StoreData>
        ) : Parcelable

        @Parcelize
        data class MediaData(
            val id: Int,
            val collection_name: String,
            val original: String,
            val medium: String,
            val thumbnail: String
        ) : Parcelable

        @Parcelize
        data class StoreData(
            val id: Int,
            val name: String,
            val fullname: String,
            val address: String,
            val color: String
        ) : Parcelable

        fun getPointText(): String {
            return NumberFormat.getInstance().format(point)
        }

        fun getOriginalUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.original
        }

        fun getMediumUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.medium
        }

        fun getThumbnailUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.thumbnail
        }

        @ColorInt
        fun getBrandColor(): Int {
            return stores.storeData.getOrNull(0)?.takeIf { it.color.isNotNullOrEmpty() }?.let { Color.parseColor(it.color) }
                ?: ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
        }
    }

    data class Meta(val pagination: Pagination) {
        data class Pagination(
            val total: Int,
            val count: Int,
            val per_page: Int,
            val current_page: Int,
            val total_pages: Int,
            val links: Links
        ) {
            data class Links(
                val next: String
            )
        }
    }
}