package com.hsvibe.model.entities

import com.hsvibe.model.UserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
class UserInfoEntity {

    val firstName: String
    val lastName: String
    val mobileNumber: String
    val gender: String
    val birthday: String
    val referNo: String
    val referrerNo: String
    val deviceType: String
    val deviceModel: String
    val createdTime: String
    val updatedTime: String
    val regionName: String
    val regionZip: String

    constructor(userInfo: UserInfo) {
        this.firstName = userInfo.getFirstName()
        this.lastName = userInfo.getLastName()
        this.mobileNumber = userInfo.getMobileNumber()
        this.gender = userInfo.getGender()
        this.birthday = userInfo.getBirthday()
        this.referNo = userInfo.getReferNo()
        this.referrerNo = userInfo.getReferrerNo()
        this.deviceType = userInfo.getDeviceType()
        this.deviceModel = userInfo.getDeviceModel()
        this.createdTime = userInfo.getCreatedTime()
        this.updatedTime = userInfo.getUpdatedTime()
        this.regionName = userInfo.getRegionName() ?: ""
        this.regionZip = userInfo.getRegionZip() ?: ""
    }


}