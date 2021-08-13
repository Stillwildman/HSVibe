package com.hsvibe.model.posts

/**
 * Created by Vincent on 2021/7/5.
 */
data class PostUpdateUserInfo(
    val first_name: String? = null,
    val last_name: String? = null,
    val mobile_number: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val device_type: String? = null,
    val device_model: String? = null,
    val lat: String? = null,
    val long: String? = null,
    val pay_password: String? = null
)
