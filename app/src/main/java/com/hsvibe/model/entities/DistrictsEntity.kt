package com.hsvibe.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.hsvibe.model.DBParams

/**
 * Created by Vincent on 2021/8/14.
 */
data class DistrictsEntity(
    @Embedded
    val city: RegionCityEntity,

    @Relation(
        entity = RegionPostalEntity::class,
        parentColumn = DBParams.COLUMN_ID,
        entityColumn = DBParams.COLUMN_CITY_ID
    )
    val postalList: List<RegionPostalEntity>
)