package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/7/25.
 */
data class ItemBanner(
    @SerializedName("data")
    val contentData: List<ContentData>
) {
    data class ContentData(
        val uuid: String,
        val title: String,
        val share_url: String,
        val approval_at: String,
        val expire_at: String,
        val media: Media
    ) {
        data class Media(
            @SerializedName("data")
            val mediaData: List<MediaData>
        ) {
            data class MediaData(
                val id: Int,
                val collection_name: String,
                val custom_properties: List<Any>,
                val original: String,
                val medium: String,
                val thumbnail: String
            )
        }

        fun getThumbnail(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.thumbnail
        }

        fun getMedium(): String? {
            return media.mediaData.takeIf { it.isNotEmpty() }?.get(0)?.medium
        }
    }
}





