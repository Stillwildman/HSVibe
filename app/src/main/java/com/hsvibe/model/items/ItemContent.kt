package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/7/19.
 */
data class ItemContent(
    @SerializedName("data")
    val contentData: List<ContentData>,
    val meta: Meta
) {
    data class ContentData(
        val uuid: String,
        val title: String,
        val brief: String,
        val content: String,
        val share_url: String,
        val approval_at: String,
        val categories: Categories?,
        val media: Media?
    ) {
        data class Categories(
            @SerializedName("data")
            val categoryData: List<CategoryData>
        )

        data class Media(
            @SerializedName("data")
            val mediaData: List<MediaData>
        )

        data class CategoryData(
            val id: Int,
            val parent_id: Int,
            val name: String,
            val ancestors_path: String
        )

        data class MediaData(
            val id: Int,
            val collection_name: String,
            val custom_properties: List<Any>,
            val original: String,
            val thumbnail: String,
            val medium: String
        )

        fun getCategoryName(): String? {
            return categories?.categoryData.takeIf { it.isNullOrEmpty() }?.get(0)?.name
        }

        fun getThumbnailUrl(): String? {
            return media?.mediaData?.takeIf { it.isNotEmpty() }?.get(0)?.thumbnail
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