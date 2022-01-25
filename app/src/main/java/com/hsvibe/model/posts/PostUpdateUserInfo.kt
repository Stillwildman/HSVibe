package com.hsvibe.model.posts

/**
 * Created by Vincent on 2021/7/5.
 */
data class PostUpdateUserInfo(
    var first_name: String? = null,
    var last_name: String? = null,
    var mobile_number: String? = null,
    var gender: String? = null,
    var birthday: String? = null,
    var device_type: String? = null,
    var device_model: String? = null,
    var referrer_no: String? = null,
    var region_zip: String? = null,
    var lat: String? = null,
    var long: String? = null,
    var pay_password: String? = null,
    var fcm_token: String? = null
)
