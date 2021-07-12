package com.hsvibe.model.posts

/**
 * Created by Vincent on 2021/7/5.
 */
data class PostUpdateUserInfo(
    val first_name: String,
    val last_name: String,
    val mobile_number: String,
    val gender: String,
    val birthday: String,
    val device_type: String,
    val device_model: String,
    val lat: String,
    val long: String
)
