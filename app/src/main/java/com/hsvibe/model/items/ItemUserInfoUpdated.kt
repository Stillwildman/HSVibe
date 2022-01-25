package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
import com.hsvibe.model.UserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
data class ItemUserInfoUpdated(
    @SerializedName("data")
    val userData: UserInfo,
    val message: String
) : UserInfo {
    data class UserInfo(
        val uuid: String,
        val first_name: String,
        val last_name: String,
        val mobile_number: String,
        val gender: String,
        val birthday: String,
        val device_type: String,
        val device_model: String,
        val refer_no: String,
        val referrer_no: String,
        val expiring_point: String,
        val coupon: Int,
        val balance: Int,
        val accumulate: Int,
        val is_set_paypassword: Boolean,
        val created_at: String,
        val updated_at: String,
        val regions: Regions
    )

    data class Regions(
        @SerializedName("data")
        val regionData: List<RegionInfo>
    )

    data class RegionInfo(
        val name: String,
        val zip_code: String,
        val parent_id: Int
    )

    override fun getUuid(): String = userData.uuid

    override fun getFirstName(): String = userData.first_name

    override fun getLastName(): String = userData.last_name

    override fun getMobileNumber(): String = userData.mobile_number

    override fun getGender(): String = userData.gender

    override fun getBirthday(): String = userData.birthday

    override fun getReferNo(): String = userData.refer_no

    override fun getReferrerNo(): String = userData.referrer_no

    override fun getExpiringPoint(): String = userData.expiring_point

    override fun getCouponAmount(): Int = userData.coupon

    override fun getBalance(): Int = userData.balance

    override fun getAccumulate(): Int = userData.accumulate

    override fun isSetPassword(): Boolean = userData.is_set_paypassword

    override fun getDeviceType(): String = userData.device_type

    override fun getDeviceModel(): String = userData.device_model

    override fun getCreatedTime(): String = userData.created_at

    override fun getUpdatedTime(): String = userData.updated_at

    override fun getRegionName(): String? {
        return userData.regions.regionData.takeIf { it.isNotEmpty() }?.let { it[0].name }
    }

    override fun getRegionZip(): String? {
        return userData.regions.regionData.takeIf { it.isNotEmpty() }?.let { it[0].zip_code }
    }

    override fun getRegionParentId(): Int {
        return userData.regions.regionData.takeIf { it.isNotEmpty() }?.let { it[0].parent_id } ?: 0
    }
}