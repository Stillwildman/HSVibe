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
data class UserInfoEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = DBParams.COLUMN_ID)
    val id: Int = DBParams.DEFAULT_USER_ID,

    @ColumnInfo(name = DBParams.COLUMN_UUID)
    val column_uuid: String,

    @ColumnInfo(name = DBParams.COLUMN_FIRST_NAME)
    val column_firstName: String,

    @ColumnInfo(name = DBParams.COLUMN_LAST_NAME)
    val column_lastName: String,

    @ColumnInfo(name = DBParams.COLUMN_MOBILE_NUMBER)
    val column_mobileNumber: String,

    @ColumnInfo(name = DBParams.COLUMN_GENDER)
    val column_gender: String,

    @ColumnInfo(name = DBParams.COLUMN_BIRTHDAY)
    val column_birthday: String,

    @ColumnInfo(name = DBParams.COLUMN_REFER_NO)
    val column_referNo: String,

    @ColumnInfo(name = DBParams.COLUMN_REFERRER_NO)
    val column_referrerNo: String,

    @ColumnInfo(name = DBParams.COLUMN_DEVICE_TYPE)
    val column_deviceType: String,

    @ColumnInfo(name = DBParams.COLUMN_DEVICE_MODEL)
    val column_deviceModel: String,

    @ColumnInfo(name = DBParams.COLUMN_CREATED_TIME)
    val column_createdTime: String,

    @ColumnInfo(name = DBParams.COLUMN_UPDATED_TIME)
    val column_updatedTime: String,

    @ColumnInfo(name = DBParams.COLUMN_REGION_NAME)
    val column_regionName: String,

    @ColumnInfo(name = DBParams.COLUMN_REGION_ZIP)
    val column_regionZip: String

) : UserInfo {

    constructor(userInfo: UserInfo) : this(
        column_uuid = userInfo.getUuid(),
        column_firstName = userInfo.getFirstName(),
        column_lastName = userInfo.getLastName(),
        column_mobileNumber = userInfo.getMobileNumber(),
        column_gender = userInfo.getGender(),
        column_birthday = userInfo.getBirthday(),
        column_referNo = userInfo.getReferNo(),
        column_referrerNo = userInfo.getReferrerNo(),
        column_deviceType = userInfo.getDeviceType(),
        column_deviceModel = userInfo.getDeviceModel(),
        column_createdTime = userInfo.getCreatedTime(),
        column_updatedTime = userInfo.getUpdatedTime(),
        column_regionName = userInfo.getRegionName() ?: "",
        column_regionZip = userInfo.getRegionZip() ?: "",
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