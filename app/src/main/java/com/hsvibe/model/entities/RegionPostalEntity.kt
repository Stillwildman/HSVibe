package com.hsvibe.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.hsvibe.model.DBParams

/**
 * Created by Vincent on 2021/8/14.
 */
@Entity(
    tableName = DBParams.TABLE_REGION_POSTAL,
    foreignKeys = [
        ForeignKey(
            entity = RegionCityEntity::class,
            parentColumns = [DBParams.COLUMN_ID],
            childColumns = [DBParams.COLUMN_CITY_ID]
        )]
)
class RegionPostalEntity(
    @ColumnInfo(name = DBParams.COLUMN_CITY_ID)
    val cityId: Int,

    @ColumnInfo(name = DBParams.COLUMN_AREA_NAME)
    val name: String,

    @PrimaryKey
    @ColumnInfo(name = DBParams.COLUMN_ZIP_CODE)
    val zipCode: String
)