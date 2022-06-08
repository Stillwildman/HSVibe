package com.hsvibe.network

import com.hsvibe.model.ApiConst
import com.hsvibe.model.Urls
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.*
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
        @Query(ApiConst.STORE_ID) storeId: String?,
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

    @GET(Urls.API_COUPON_BRAND)
    suspend fun getCouponBrands(@Query(ApiConst.CATEGORY_ID) category: Int?, @Query(ApiConst.PARTNER_ID) partnerId: Int?): Response<ItemBrand>

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
    ): Response<ItemPayloadCode>

    @POST(Urls.API_VERIFY_PAY_PASSWORD)
    suspend fun verifyPayPassword(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Body body: PostPassword
    ): Response<ItemMessage>

    @GET(Urls.API_CARD_LINK)
    suspend fun getCreditCards(
        @Header(ApiConst.AUTHORIZATION) auth: String
    ): Response<ItemCardList>

    @POST("${Urls.API_CARD_LINK_DELETE}/{key}")
    suspend fun deleteCreditCard(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Path("key") key: String
    ): Response<ItemCardList>

    @POST(Urls.API_PAYMENT_PAYLOAD)
    suspend fun getPaymentCode(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Body body: PostPaymentPayload
    ): Response<ItemPayloadCode>

    @GET(Urls.API_TRANSACTION_HISTORY)
    suspend fun getTransactionHistory(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Query(ApiConst.ORDER_BY) orderBy: String,
        @Query(ApiConst.SORTED_BY) sortedBy: String,
        @Query(ApiConst.LIMIT) limit: Int,
        @Query(ApiConst.PAGE) page: Int
    ): Response<ItemTransactions>

    @POST(Urls.API_TRANSFER_POINT)
    suspend fun transferPoint(
        @Header(ApiConst.AUTHORIZATION) auth: String,
        @Body body: PostTransferPoint
    ): Response<ItemPointTransfer>
}