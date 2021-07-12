package com.hsvibe.network

import com.hsvibe.model.Const
import com.hsvibe.model.Urls
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.ItemUserInfo
import com.hsvibe.model.items.ItemUserInfoUpdated
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Vincent on 2021/6/28.
 */
interface ApiInterface {

    @POST
    fun refreshToken(@Url url: String, @Body body: PostRefreshToken): Observable<Response<UserToken>>

    @POST
    suspend fun refreshTokenSuspend(@Url url: String, @Body body: PostRefreshToken): Response<UserToken>

    @GET(Urls.API_USER_INFO)
    suspend fun getUserInfo(@Header(Const.AUTHORIZATION) auth: String?): Response<ItemUserInfo>

    @POST(Urls.API_USER_INFO)
    suspend fun updateUserInfo(@Header(Const.AUTHORIZATION) auth: String?, @Body body: PostUpdateUserInfo): Response<ItemUserInfoUpdated>
}