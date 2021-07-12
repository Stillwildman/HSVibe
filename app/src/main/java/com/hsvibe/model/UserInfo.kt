package com.hsvibe.model

/**
 * Created by Vincent on 2021/7/5.
 */
interface UserInfo {

    fun getFirstName(): String
    fun getLastName(): String
    fun getMobileNumber(): String
    fun getGender(): String
    fun getBirthday(): String
    fun getReferNo(): String
    fun getReferrerNo(): String
    fun getDeviceType(): String
    fun getDeviceModel(): String
    fun getCreatedTime(): String
    fun getUpdatedTime(): String
    fun getRegionName(): String?
    fun getRegionZip(): String?

    fun getLogInfo(): String {
        return "FirstName: ${getFirstName()}\n" +
                "LastName: ${getLastName()}\n" +
                "MobileNumber: ${getMobileNumber()}\n" +
                "Gender: ${getGender()}\n" +
                "Birthday: ${getBirthday()}\n" +
                "ReferNo: ${getReferNo()}\n" +
                "ReferrerNo: ${getReferrerNo()}\n" +
                "DeviceType: ${getDeviceType()}\n" +
                "DeviceModel: ${getDeviceModel()}\n" +
                "CreatedTime: ${getCreatedTime()}\n" +
                "UpdatedTime: ${getUpdatedTime()}\n" +
                "RegionName: ${getRegionName()}\n" +
                "RegionZip: ${getRegionZip()}"
    }
}