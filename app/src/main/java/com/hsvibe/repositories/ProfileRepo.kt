package com.hsvibe.repositories

import com.hsvibe.model.UserInfo
import com.hsvibe.model.entities.DistrictsEntity

/**
 * Created by Vincent on 2021/8/18.
 */
interface ProfileRepo : LoadingCallbackRepo {

    fun getUserInfo(): UserInfo?

    suspend fun getDistricts(): List<DistrictsEntity>?

    suspend fun getGenderPairList(): List<Pair<String, String?>>

    fun getGenderSelection(): Int
}