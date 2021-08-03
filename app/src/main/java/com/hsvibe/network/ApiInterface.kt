package com.hsvibe.network

import com.hsvibe.model.ApiConst
import com.hsvibe.model.Urls
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Vincent on 2021/6/28.
 */
interface ApiInterface {

    @POST
    suspend fun refreshToken(@Url url: String, @Body body: PostRefreshToken): Response<UserToken>

    @GET(Urls.API_USER_INFO)
    suspend fun getUserInfo(@Header(ApiConst.AUTHORIZATION) auth: String?): Response<ItemUserInfo>

    @POST(Urls.API_USER_INFO)
    suspend fun updateUserInfo(@Header(ApiConst.AUTHORIZATION) auth: String?, @Body body: PostUpdateUserInfo): Response<ItemUserInfoUpdated>

    @GET(Urls.API_CONTENT)
    suspend fun getContent(
        @Query(ApiConst.CATEGORY_ID) category: Int = ApiConst.CATEGORY_NEWS,
        @Query(ApiConst.ORDER_BY) orderBy: String = ApiConst.ORDER_BY_UPDATED,
        @Query(ApiConst.SORTED_BY) sortedBy: String = ApiConst.SORTED_BY_DESC,
        @Query(ApiConst.LIMIT) limit: Int = ApiConst.DEFAULT_LIMIT,
        @Query(ApiConst.PAGE) page: Int = 1
    ): Response<ItemContent>

    @GET(Urls.API_COUPON)
    suspend fun getCoupon(
        @Query(ApiConst.ORDER_BY) orderBy: String = ApiConst.ORDER_BY_UPDATED,
        @Query(ApiConst.SORTED_BY) sortedBy: String = ApiConst.SORTED_BY_DESC,
        @Query(ApiConst.LIMIT) limit: Int = ApiConst.DEFAULT_LIMIT,
        @Query(ApiConst.PAGE) page: Int = 1
    ): Response<ItemCoupon>

    @GET(Urls.API_BANNER)
    suspend fun getBanner(
        @Query(ApiConst.ORDER_BY) orderBy: String = ApiConst.ORDER_BY_UPDATED,
        @Query(ApiConst.SORTED_BY) sortedBy: String = ApiConst.SORTED_BY_DESC,
        @Query(ApiConst.LIMIT) limit: Int = 5
    ): Response<ItemBanner>
}