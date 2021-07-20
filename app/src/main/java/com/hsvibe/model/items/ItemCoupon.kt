package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/7/20.
 */
data class ItemCoupon(
    @SerializedName("data")
    val contentData: List<ContentData>,
    val meta: Meta
) {
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
    ) {
        data class Media(
            @SerializedName("data")
            val mediaData: List<MediaData>
        )

        data class Stores(
            @SerializedName("data")
            val storyData: List<StoryData>
        )

        data class MediaData(
            val id: Int,
            val collection_name: String,
            val custom_properties: List<Any>,
            val original: String,
            val medium: String,
            val thumbnail: String
        )

        data class StoryData(
            val id: Int,
            val name: String,
            val fullname: String,
            val address: String,
            val color: String
        )

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