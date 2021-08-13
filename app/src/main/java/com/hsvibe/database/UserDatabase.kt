package com.hsvibe.database

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hsvibe.AppController
import com.hsvibe.database.dao.DaoUserInfo
import com.hsvibe.model.DBParams
import com.hsvibe.model.entities.UserInfoEntity
import java.lang.ref.SoftReference

/**
 * Created by Vincent on 2021/8/13.
 */
@Database(entities = [(UserInfoEntity::class)], version = DBParams.DB_VERSION)
abstract class UserDatabase : RoomDatabase() {

    companion object {
        private object SingleDB {
            var INSTANCE: SoftReference<UserDatabase>? = null
        }

        fun getInstance(): UserDatabase {
            if (SingleDB.INSTANCE == null || SingleDB.INSTANCE?.get() == null) {
                SingleDB.INSTANCE = SoftReference(getDatabase())
            }
            return SingleDB.INSTANCE?.get()!!
        }

        private fun getDatabase() : UserDatabase {
            return Room.databaseBuilder(AppController.getAppContext(), UserDatabase::class.java, DBParams.DB_NAME_USER)
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.i("PoiDatabase", "onMigrating!!! Current version: " + database.version)
                // TODO Do migration if needed.
                Log.i("PoiDatabase", "Migration DONE!!!")
            }
        }
    }

    abstract fun getUserInfoDao(): DaoUserInfo
}