package com.hsvibe.model

import com.hsvibe.AppController
import com.hsvibe.R

/**
 * Created by Vincent on 2021/7/5.
 */
interface UserInfo {

    fun getUuid(): String
    fun getFirstName(): String
    fun getLastName(): String
    fun getName(): String = "${getFirstName()} ${getLastName()}".trim().takeIf { it.isNotEmpty() } ?: AppController.getString(R.string.there)
    fun getMobileNumber(): String
    fun getGender(): String
    fun getBirthday(): String
    fun getReferNo(): String
    fun getReferrerNo(): String
    fun getExpiringPoint(): String
    fun getCouponAmount(): Int
    fun getBalance(): Int
    fun getAccumulate(): Int
    fun isSetPassword(): Boolean
    fun getDeviceType(): String
    fun getDeviceModel(): String
    fun getCreatedTime(): String
    fun getUpdatedTime(): String
    fun getRegionName(): String?
    fun getRegionZip(): String?
    fun getRegionParentId(): Int

    fun getLogInfo(): String {
        return "UUID: ${getUuid()}\n" +
                "FirstName: ${getFirstName()}\n" +
                "LastName: ${getLastName()}\n" +
                "MobileNumber: ${getMobileNumber()}\n" +
                "Gender: ${getGender()}\n" +
                "Birthday: ${getBirthday()}\n" +
                "ReferNo: ${getReferNo()}\n" +
                "ReferrerNo: ${getReferrerNo()}\n" +
                "ExpiringPoint: ${getExpiringPoint()}\n" +
                "CouponAmount: ${getCouponAmount()}\n" +
                "Balance: ${getBalance()}\n" +
                "Accumulate: ${getAccumulate()}\n" +
                "IsSetPassword: ${isSetPassword()}\n" +
                "DeviceType: ${getDeviceType()}\n" +
                "DeviceModel: ${getDeviceModel()}\n" +
                "CreatedTime: ${getCreatedTime()}\n" +
                "UpdatedTime: ${getUpdatedTime()}\n" +
                "RegionName: ${getRegionName()}\n" +
                "RegionZip: ${getRegionZip()}\n" +
                "RegionParentId: ${getRegionParentId()}\n"

    }
}