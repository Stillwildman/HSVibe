package com.hsvibe.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hsvibe.model.DBParams
import com.hsvibe.model.UserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
@Suppress("PropertyName")
@Entity(tableName = DBParams.TABLE_USER_INFO)
class UserInfoEntity : UserInfo {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = DBParams.COLUMN_ID)
    var id: Int = DBParams.DEFAULT_USER_ID

    @ColumnInfo(name = DBParams.COLUMN_UUID)
    var column_uuid: String

    @ColumnInfo(name = DBParams.COLUMN_FIRST_NAME)
    var column_firstName: String

    @ColumnInfo(name = DBParams.COLUMN_LAST_NAME)
    var column_lastName: String

    @ColumnInfo(name = DBParams.COLUMN_MOBILE_NUMBER)
    var column_mobileNumber: String

    @ColumnInfo(name = DBParams.COLUMN_GENDER)
    var column_gender: String

    @ColumnInfo(name = DBParams.COLUMN_BIRTHDAY)
    var column_birthday: String

    @ColumnInfo(name = DBParams.COLUMN_REFER_NO)
    var column_referNo: String

    @ColumnInfo(name = DBParams.COLUMN_REFERRER_NO)
    var column_referrerNo: String

    @ColumnInfo(name = DBParams.COLUMN_DEVICE_TYPE)
    var column_deviceType: String

    @ColumnInfo(name = DBParams.COLUMN_DEVICE_MODEL)
    var column_deviceModel: String

    @ColumnInfo(name = DBParams.COLUMN_CREATED_TIME)
    var column_createdTime: String

    @ColumnInfo(name = DBParams.COLUMN_UPDATED_TIME)
    var column_updatedTime: String

    @ColumnInfo(name = DBParams.COLUMN_REGION_NAME)
    var column_regionName: String

    @ColumnInfo(name = DBParams.COLUMN_REGION_ZIP)
    var column_regionZip: String

    constructor(
        column_uuid: String,
        column_firstName: String,
        column_lastName: String,
        column_mobileNumber: String,
        column_gender: String,
        column_birthday: String,
        column_referNo: String,
        column_referrerNo: String,
        column_deviceType: String,
        column_deviceModel: String,
        column_createdTime: String,
        column_updatedTime: String,
        column_regionName: String?,
        column_regionZip: String?
    ) {
        this.column_uuid = column_uuid
        this.column_firstName = column_firstName
        this.column_lastName = column_lastName
        this.column_mobileNumber = column_mobileNumber
        this.column_gender = column_gender
        this.column_birthday = column_birthday
        this.column_referNo = column_referNo
        this.column_referrerNo = column_referrerNo
        this.column_deviceType = column_deviceType
        this.column_deviceModel = column_deviceModel
        this.column_createdTime = column_createdTime
        this.column_updatedTime = column_updatedTime
        this.column_regionName = column_regionName ?: ""
        this.column_regionZip = column_regionZip ?: ""
    }

    constructor(userInfo: UserInfo) : this(
        userInfo.getUuid(),
        userInfo.getFirstName(),
        userInfo.getLastName(),
        userInfo.getMobileNumber(),
        userInfo.getGender(),
        userInfo.getBirthday(),
        userInfo.getReferNo(),
        userInfo.getReferrerNo(),
        userInfo.getDeviceType(),
        userInfo.getDeviceModel(),
        userInfo.getCreatedTime(),
        userInfo.getUpdatedTime(),
        userInfo.getRegionName(),
        userInfo.getRegionZip(),
    )

    override fun getUuid(): String = column_uuid

    override fun getFirstName(): String = column_firstName

    override fun getLastName(): String = column_lastName

    override fun getMobileNumber(): String = column_mobileNumber

    override fun getGender(): String = column_gender

    override fun getBirthday(): String = column_birthday

    override fun getReferNo(): String = column_referNo

    override fun getReferrerNo(): String = column_referrerNo

    override fun getDeviceType(): String = column_deviceType

    override fun getDeviceModel(): String = column_deviceModel

    override fun getCreatedTime(): String = column_createdTime

    override fun getUpdatedTime(): String = column_updatedTime

    override fun getRegionName(): String = column_regionName

    override fun getRegionZip(): String = column_regionZip

    override fun getPayPassword(): String? = null

}