package com.hsvibe.model.items

import android.view.View
import com.google.gson.annotations.SerializedName
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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
        val created_at: String,
        val updated_at: String,
        val categories: Categories?,
        val media: Media?,
        var isUnread: Boolean?
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
            return categories?.categoryData?.takeIf { it.isNotEmpty() }?.get(0)?.name
        }

        fun getOriginalUrl(): String? {
            return media?.mediaData?.takeIf { it.isNotEmpty() }?.get(0)?.original
        }

        fun getMediumUrl(): String? {
            return media?.mediaData?.takeIf { it.isNotEmpty() }?.get(0)?.medium
        }

        fun getThumbnailUrl(): String? {
            return media?.mediaData?.takeIf { it.isNotEmpty() }?.get(0)?.thumbnail
        }

        fun setUnreadStatus(lastNewestTime: Long, dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())) {
            if (isUnread == false) {
                return
            }
            try {
                val time: Long = dateFormat.parse(approval_at)?.time ?: 0L
                isUnread = time > lastNewestTime
            }
            catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        fun getUnreadDotVisibility(): Int {
            return if (isUnread == true) View.VISIBLE else View.GONE
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