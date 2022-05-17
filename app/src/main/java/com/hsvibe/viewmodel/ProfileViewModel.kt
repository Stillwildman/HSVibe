package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hsvibe.model.Const
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.repositories.ProfileRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/8/15.
 */
class ProfileViewModel(private val profileRepo: ProfileRepo) : LoadingStatusViewModel() {

    init {
        profileRepo.setLoadingCallback(this)
    }

    val livePostUserInfo by lazy { MutableLiveData<PostUpdateUserInfo>() }

    val liveGenderPairList by lazy { MutableLiveData<List<Pair<String, String?>>>() }

    val liveCityPairList by lazy { MutableLiveData<List<Pair<String, String?>>>() }

    val postalPairList by lazy { mutableListOf<List<Pair<String, String?>>>() }

    val liveGenderSelection by lazy { MutableLiveData<Int>() }
    val liveCitySelection by lazy { MutableLiveData<Int>() }
    val livePostalSelection by lazy { MutableLiveData<Int>() }

    fun setupPostUserInfo() {
        PostUpdateUserInfo(
            first_name = profileRepo.getUserInfo()?.getFirstName(),
            last_name = profileRepo.getUserInfo()?.getLastName(),
            mobile_number = profileRepo.getUserInfo()?.getMobileNumber(),
            gender = profileRepo.getUserInfo()?.getGender(),
            birthday = profileRepo.getUserInfo()?.getBirthday(),
            referrer_no = profileRepo.getUserInfo()?.getReferrerNo()?.takeIf { it.length >= Const.REFERRER_NO_LENGTH_LIMIT }
        ).also {
            livePostUserInfo.value = it
        }
    }

    fun setupGenderList() {
        viewModelScope.launch {
            profileRepo.getGenderPairList().let {
                liveGenderPairList.value = it
                liveGenderSelection.postValue(profileRepo.getGenderSelection())
            }
        }
    }

    fun setupDistrictsList() {
        viewModelScope.launch(Dispatchers.Default) {
            profileRepo.getDistricts()?.let {
                val userRegionZip = profileRepo.getUserInfo()?.getRegionZip()
                var citySelection = -1
                var postalSelection = -1

                val cityPairList = mutableListOf<Pair<String, String?>>()

                it.forEachIndexed { cityIndex, district ->
                    cityPairList.add(Pair(district.city.name, district.city.id.toString()))

                    val postalList = mutableListOf<Pair<String, String?>>()

                    district.postalList.forEachIndexed { postalIndex, postal ->
                        postalList.add(Pair(postal.name, postal.zipCode))

                        if (postalSelection == -1 && !userRegionZip.isNullOrEmpty() && postal.zipCode == userRegionZip) {
                            postalSelection = postalIndex
                        }
                    }
                    postalPairList.add(postalList)

                    if (citySelection == -1 && postalSelection != -1) {
                        citySelection = cityIndex
                    }
                }
                withContext(Dispatchers.Main) {
                    liveCityPairList.value = cityPairList
                    if (citySelection != -1 && postalSelection != -1) {
                        delay(150)
                        liveCitySelection.postValue(citySelection + 1) // It + 1 because CityPairList has added a hint header.
                        delay(100)
                        livePostalSelection.postValue(postalSelection)
                    }
                }
            }
        }
    }

    fun isReferrerNoEntered(): Boolean {
        return (livePostUserInfo.value?.referrer_no?.length ?: 0) >= Const.REFERRER_NO_LENGTH_LIMIT
    }
}