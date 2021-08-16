package com.hsvibe.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hsvibe.model.DBParams

/**
 * Created by Vincent on 2021/8/14.
 */
@Entity(tableName = DBParams.TABLE_REGION_CITIES)
data class RegionCityEntity(
    @PrimaryKey
    @ColumnInfo(name = DBParams.COLUMN_ID)
    val id: Int,

    @ColumnInfo(name = DBParams.COLUMN_CITY_NAME)
    val name: String,
)