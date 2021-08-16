package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/8/14.
 */
data class ItemDistricts(
    @SerializedName("data")
    val regionData: List<RegionData>
) {
    data class RegionData(
        val id: Int,
        val name: String,
        val regions: Regions
    ) {
        data class Regions(
            @SerializedName("data")
            val zipData: List<ZipData>
        ) {
            data class ZipData(
                val id: Int,
                val name: String,
                val zip_code: String
            )
        }
    }
}


