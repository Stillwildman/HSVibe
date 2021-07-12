package com.hsvibe.network

import com.hsvibe.callbacks.OnDataGetCallback
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.Urls
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserToken
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Created by Vincent on 2021/6/28.
 */
object DataCallbacks {

    private const val TAG = "DataCallbacks"

    private fun getApiInterface(baseUrl: String = Urls.BASE_API_URL): ApiInterface {
        return RetrofitAgent.getRetrofit(baseUrl).create(ApiInterface::class.java)
    }

    private fun <ResponseItem : Response<Item>, Item> getData(observable: Observable<ResponseItem>, callback: OnDataGetCallback<Item>, loadingCallback: OnLoadingCallback? = null) {
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(loading(loadingCallback))
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseItem> {
                override fun onSubscribe(d: Disposable) {
                    L.i(TAG, "onSubscribe!!!")
                }

                override fun onNext(response: ResponseItem) {
                    L.i(TAG, "onNext!!!")

                    callback.onDataGet(response.body())
                }

                override fun onError(e: Throwable) {
                    L.i(TAG, "onError!!! ${e.message}")
                    callback.onDataGetFailed(e.message ?: "")
                }

                override fun onComplete() {
                    L.i(TAG, "onComplete!!!")
                }
            })
    }

    private fun <T> loading(loadingCallback: OnLoadingCallback?): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> { upstream ->
            upstream.doOnSubscribe {
                loadingCallback?.onLoadingStart()
            }.doFinally {
                loadingCallback?.onLoadingEnd()
            }
        }
    }

    private fun <ItemType> getResponseResult(response: Response<ItemType>, loadingCallback: OnLoadingCallback?): ItemType? {
        L.d("Suspend API Call! Thread is ${Thread.currentThread().name}")

        return if (response.isSuccessful) {
            loadingCallback?.onLoadingEnd()
            response.body() as ItemType
        }
        else {
            val errorMessage = response.errorBody()?.charStream()?.readText()
            loadingCallback?.onLoadingFailed(errorMessage)
            loadingCallback?.onLoadingEnd()
            throw ApiStatusException(response.code(), errorMessage)
        }
    }

    suspend fun refreshUserToken(postBody: PostRefreshToken, loadingCallback: OnLoadingCallback?): UserToken? {
        return withContext(Dispatchers.IO) {
            loadingCallback?.onLoadingStart()
            val response = getApiInterface().refreshTokenSuspend(Urls.API_REFRESH_TOKEN, postBody)
            getResponseResult(response, loadingCallback)
        }
    }

    suspend fun getUserInfo(auth: String, loadingCallback: OnLoadingCallback? = null): UserInfo? {
        return withContext(Dispatchers.IO) {
            loadingCallback?.onLoadingStart()

            val response = getApiInterface().getUserInfo(auth)

            getResponseResult(response, loadingCallback)
        }
    }

    suspend fun updateUserInfo(auth: String, postBody: PostUpdateUserInfo, loadingCallback: OnLoadingCallback? = null): UserInfo? {
        return withContext(Dispatchers.IO) {
            loadingCallback?.onLoadingStart()

            val response = getApiInterface().updateUserInfo(auth, postBody)

            getResponseResult(response, loadingCallback)
        }
    }


}