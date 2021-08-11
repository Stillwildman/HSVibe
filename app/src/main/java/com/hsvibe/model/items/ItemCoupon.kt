package com.hsvibe.model.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
            val storyData: List<StoryData>
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
        data class StoryData(
            val id: Int,
            val name: String,
            val fullname: String,
            val address: String,
            val color: String
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