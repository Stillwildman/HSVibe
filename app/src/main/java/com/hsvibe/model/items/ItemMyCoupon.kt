package com.hsvibe.model.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Vincent on 2022/4/19.
 */
data class ItemMyCoupon(
    @SerializedName("data")
    val contentData: List<ContentData>
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
        val status: Int,
        val use_at: String,
        val expire_at: String,
        val created_at: String,
        val updated_at: String,
        val brand_thumb: String,
        val media: Media
    ) : Parcelable {
        @Parcelize
        data class Media(
            @SerializedName("data")
            val mediaData: List<MediaData>
        ) : Parcelable

        @Parcelize
        data class MediaData(
            val original: String,
            val medium: String,
            val thumbnail: String
        ) : Parcelable

        fun getOriginalUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.original
        }

        fun getMediumUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.medium
        }

        fun getThumbnailUrl(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.thumbnail
        }
    }
}