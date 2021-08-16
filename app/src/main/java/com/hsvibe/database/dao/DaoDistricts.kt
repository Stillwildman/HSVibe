package com.hsvibe.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hsvibe.model.entities.DistrictsEntity
import com.hsvibe.model.entities.RegionCityEntity
import com.hsvibe.model.entities.RegionPostalEntity

/**
 * Created by Vincent on 2021/8/15.
 */
@Dao
interface DaoDistricts {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegionCities(cityEntityList: List<RegionCityEntity>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRegionPostal(postalEntity: List<RegionPostalEntity>): Array<Long>

    @Query("SELECT * FROM RegionCities")
    fun getDistricts(): List<DistrictsEntity>?

    @Query("DELETE FROM RegionPostal")
    fun clearPostalTable(): Int
}