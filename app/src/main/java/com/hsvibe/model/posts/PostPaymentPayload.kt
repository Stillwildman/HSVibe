package com.hsvibe.model.posts

/**
 * Created by Vincent on 2022/5/17.
 */
data class PostPaymentPayload(
    val discount_amount: Int? = null,
    val link_key: String? = null,
    val ticket_uuid: String? = null
)
