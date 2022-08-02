package com.hsvibe.repositories

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.database.UserDatabase
import com.hsvibe.model.Const
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.entities.DistrictsEntity
import com.hsvibe.model.entities.RegionCityEntity
import com.hsvibe.model.entities.RegionPostalEntity
import com.hsvibe.model.items.ItemDistricts
import com.hsvibe.network.DataCallbacks
import com.hsvibe.utilities.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Vincent on 2021/8/15.
 */
class ProfileRepoImpl(private val _userInfo: UserInfo?) : ProfileRepo {

    private var callback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override fun getUserInfo(): UserInfo? = _userInfo

    override suspend fun getDistricts(): List<DistrictsEntity>? {
        return withContext(Dispatchers.IO) {
            UserDatabase.getInstance().getDistrictsDao().getDistricts()?.takeIf { it.isNotEmpty() } ?: createDistrictsData()
        }
    }

    private suspend fun createDistrictsData(): List<DistrictsEntity>? {
        return withContext(Dispatchers.Default) {
            val districtsItem = getDistrictsDataFromRes() ?: getDistrictsDataFromServer()

            districtsItem?.let {
                val cityList = mutableListOf<RegionCityEntity>()
                val postalList = mutableListOf<RegionPostalEntity>()

                it.regionData.forEach { region ->
                    cityList.add(RegionCityEntity(region.id, region.name))

                    region.regions.zipData.forEach { postal ->
                        postalList.add(RegionPostalEntity(region.id, postal.name, postal.zip_code))
                    }
                }

                val isCitiesInsertSuccess = withContext(Dispatchers.Default) {
                    insertCitiesIntoDB(cityList)
                }
                val isPostalInsertSuccess = withContext(Dispatchers.Default) {
                    insertPostalIntoDB(postalList)
                }

                if (isCitiesInsertSuccess && isPostalInsertSuccess) {
                    UserDatabase.getInstance().getDistrictsDao().getDistricts()
                }
                else null
            }
        }
    }

    private fun getDistrictsDataFromRes(): ItemDistricts? {
        val inputStream = AppController.getAppContext().resources.openRawResource(R.raw.districts)

        val reader = BufferedReader(InputStreamReader(inputStream))

        val sb = StringBuilder()
        var eachLine: String?

        return try {
            while (reader.readLine().also { eachLine = it } != null) {
                sb.append(eachLine)
            }
            inputStream.close()
            reader.close()

            Gson().fromJson(sb.toString(), ItemDistricts::class.java)?.also { L.i("getDistrictsDataFromRes!!!") }
        }
        catch (e: IOException) {
            e.printStackTrace()
            null
        }
        catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getDistrictsDataFromServer(): ItemDistricts? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getDistricts(it, callback)
        }
    }

    private fun insertCitiesIntoDB(cityEntityList: List<RegionCityEntity>): Boolean {
        return UserDatabase.getInstance().getDistrictsDao().insertRegionCities(cityEntityList).isNotEmpty()
    }

    private fun insertPostalIntoDB(postalEntityList: List<RegionPostalEntity>): Boolean {
        return UserDatabase.getInstance().getDistrictsDao().insertRegionPostal(postalEntityList).isNotEmpty()
    }

    override suspend fun getGenderPairList(): List<Pair<String, String?>> {
        return withContext(Dispatchers.IO) {
            val pairList = mutableListOf<Pair<String, String?>>()

            AppController.getAppContext().resources.getStringArray(R.array.gender_array).forEach {
                pairList.add(Pair(it, getGenderEngText(it)))
            }
            pairList
        }
    }

    private fun getGenderEngText(chtText: String): String {
        val engTextRes = when (chtText) {
            AppController.getString(R.string.gender_male_cht) -> R.string.gender_male_eng
            AppController.getString(R.string.gender_female_cht) -> R.string.gender_female_eng
            else -> R.string.gender_unknown_eng
        }
        return AppController.getString(engTextRes)
    }

    override fun getGenderSelection(): Int {
        return getUserInfo()?.run {
            when (getGender().lowercase()) {
                Const.EMPTY_STRING -> 0
                AppController.getString(R.string.gender_male_eng) -> 1
                AppController.getString(R.string.gender_female_eng) -> 2
                else -> 3
            }
        } ?: 0
    }
}