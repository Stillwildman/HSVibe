package com.hsvibe.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hsvibe.model.entities.UserInfoEntity

/**
 * Created by Vincent on 2021/8/13.
 */
@Dao
interface DaoUserInfo {

    @Query("SELECT * FROM UserInfo LIMIT 1")
    suspend fun getUserInfo(): UserInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfoEntity: UserInfoEntity): Long

    @Query("DELETE FROM UserInfo")
    suspend fun clearUserInfo(): Int

}