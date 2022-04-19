package com.hsvibe.network

import com.hsvibe.model.ApiConst
import com.hsvibe.model.Urls
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.PostCouponRedeem
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.model.posts.PostUseCoupon
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
        @Query(ApiConst.STORE_ID) storeId: Int,
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

    @POST("${Urls.API_COUPON_REDEEM}/{uuid}")
    suspend fun redeemCoupon(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Path("uuid") uuid: String,
        @Body body: PostCouponRedeem = PostCouponRedeem(1)
    ): Response<ItemMessage>

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


    @GET(Urls.API_COUPON_CATEGORY)
    suspend fun getCouponDistricts(@Query(ApiConst.CATEGORY_ID) category: Int = ApiConst.CATEGORY_DISTRICTS): Response<ItemCouponDistricts>

    @GET(Urls.API_COUPON_STORES)
    suspend fun getCouponStores(@Query(ApiConst.CATEGORY_ID) category: Int): Response<ItemCouponStores>

    @GET(Urls.API_TICKET_HOLDER)
    suspend fun getMyCouponList(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Query(ApiConst.ORDER_BY) orderBy: String = ApiConst.ORDER_BY_UPDATED,
        @Query(ApiConst.SORTED_BY) sortedBy: String = ApiConst.SORTED_BY_DESC
    ): Response<ItemMyCoupon>

    @POST(Urls.API_COUPON_USE)
    suspend fun useCoupon(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Body body: PostUseCoupon
    ): Response<ItemCouponCode>
}