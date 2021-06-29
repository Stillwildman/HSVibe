package com.hsvibe.model

/**
 * Created by Vincent on 2021/6/28.
 */
data class UserToken(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val expires_in: Long,
    val acquiredTime: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return StringBuilder().run {
            append("[AccessToken]: $access_token\n")
            append("[RefreshToken]: $refresh_token\n")
            append("[TokenType]: $token_type\n")
            append("[ExpiresIn]: $expires_in\n")
            append("[AcquiredTime]: $acquiredTime\n")
            //append("[IsTokenExpired]: ${isTokenExpired()}")
            toString()
        }
    }
}