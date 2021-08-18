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
    suspend fun getUserInfo(@Header(ApiConst.AUTHORIZATION) auth: String): Response<ItemUserInfo>

    @POST(Urls.API_USER_INFO)
    suspend fun updateUserInfo(@Header(ApiConst.AUTHORIZATION) auth: String, @Body body: PostUpdateUserInfo): Response<ItemUserInfoUpdated>

    @GET(Urls.API_CONTENT)
    suspend fun getContent(
        @Query(ApiConst.CATEGORY_ID) category: Int,
        @Query(ApiConst.ORDER_BY) orderBy: String,
        @Query(ApiConst.SORTED_BY) sortedBy: String,
        @Query(ApiConst.LIMIT) limit: Int,
        @Query(ApiConst.PAGE) page: Int
    ): Response<ItemContent>

    @GET(Urls.API_COUPON)
    suspend fun getCoupon(
        @Query(ApiConst.ORDER_BY) orderBy: String,
        @Query(ApiConst.SORTED_BY) sortedBy: String,
        @Query(ApiConst.LIMIT) limit: Int,
        @Query(ApiConst.PAGE) page: Int
    ): Response<ItemCoupon>

    @GET(Urls.API_BANNER)
    suspend fun getBanner(
        @Query(ApiConst.ORDER_BY) orderBy: String = ApiConst.ORDER_BY_UPDATED,
        @Query(ApiConst.SORTED_BY) sortedBy: String = ApiConst.SORTED_BY_DESC,
        @Query(ApiConst.LIMIT) limit: Int = 5
    ): Response<ItemBanner>

    @GET("${Urls.API_COUPON}/{uuid}")
    suspend fun getCouponDetail(
        @Path("uuid") uuid: String
    ): Response<ItemCoupon>

    @GET(Urls.API_USER_BONUS)
    suspend fun getUserBonus(
        @Header(ApiConst.AUTHORIZATION) auth: String
    ): Response<ItemUserBonus>

    @GET(Urls.API_ACCOUNT_BONUS)
    suspend fun getAccountBonus(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Query(ApiConst.LIMIT) limit: Int,
        @Query(ApiConst.PAGE) page: Int
    ): Response<ItemAccountBonus>

    @GET
    suspend fun getDistricts(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Url url: String = Urls.API_DISTRICTS
    ): Response<ItemDistricts>

    // TODO Need to replaced with new API
    @GET
    suspend fun getCouponDistricts(@Url url: String = Urls.API_COUPON_DISTRICTS): Response<ItemCouponCategories>

    // TODO Need to replaced with new API
    @GET
    suspend fun getCouponCategories(@Url url: String = Urls.API_COUPON_CATEGORIES): Response<ItemCouponCategories>
}