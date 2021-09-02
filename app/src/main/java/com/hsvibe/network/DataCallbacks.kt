package com.hsvibe.network

import com.hsvibe.callbacks.OnDataGetCallback
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Urls
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.*
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

    private suspend fun <ItemType> getApiResult(loadingCallback: OnLoadingCallback? = null, makeApiCall:suspend () -> Response<ItemType>): ItemType? {
        return withContext(Dispatchers.IO) {
            L.d("Suspend API Call! Thread is ${Thread.currentThread().name}")

            loadingCallback?.onLoadingStart()

            val response = makeApiCall()

            if (response.isSuccessful) {
                loadingCallback?.onLoadingEnd()
                response.body()
            }
            else {
                val errorMessage = response.errorBody()?.charStream()?.readText()
                loadingCallback?.onLoadingFailed(errorMessage)
                loadingCallback?.onLoadingEnd()
                throw ApiStatusException(response.code(), errorMessage)
            }
        }
    }

    suspend fun refreshUserToken(postBody: PostRefreshToken, loadingCallback: OnLoadingCallback?): UserToken? {
        return getApiResult(loadingCallback) {
            getApiInterface().refreshToken(Urls.API_REFRESH_TOKEN, postBody)
        }
    }

    suspend fun getUserInfo(auth: String, loadingCallback: OnLoadingCallback? = null): UserInfo? {
        return getApiResult(loadingCallback) {
            getApiInterface().getUserInfo(auth)
        }
    }

    suspend fun updateUserInfo(auth: String, postBody: PostUpdateUserInfo, loadingCallback: OnLoadingCallback? = null): UserInfo? {
        return getApiResult(loadingCallback) {
            getApiInterface().updateUserInfo(auth, postBody)
        }
    }

    suspend fun getContent(category: Int,
                           orderBy: String = ApiConst.ORDER_BY_UPDATED,
                           sortedBy: String = ApiConst.SORTED_BY_DESC,
                           limit: Int = ApiConst.DEFAULT_LIMIT,
                           page: Int = 1,
                           loadingCallback: OnLoadingCallback? = null
    ): ItemContent? {
        return getApiResult(loadingCallback) {
            getApiInterface().getContent(category, orderBy, sortedBy, limit, page)
        }
    }

    suspend fun getCoupon(orderBy: String = ApiConst.ORDER_BY_UPDATED,
                          sortedBy: String = ApiConst.SORTED_BY_DESC,
                          storeId: Int = ApiConst.ALL,
                          limit: Int = ApiConst.DEFAULT_LIMIT,
                          page: Int = 1,
                          loadingCallback: OnLoadingCallback? = null
    ): ItemCoupon? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCoupon(orderBy, sortedBy, storeId, limit, page)
        }
    }

    suspend fun redeemCoupon(uuid: String, loadingCallback: OnLoadingCallback?): ItemCoupon? {
        return getApiResult(loadingCallback) {
            getApiInterface().redeemCoupon(uuid)
        }
    }

    suspend fun getBanner(loadingCallback: OnLoadingCallback? = null): ItemBanner? {
        return getApiResult(loadingCallback) {
            getApiInterface().getBanner()
        }
    }

    suspend fun getCouponDetail(uuid: String, loadingCallback: OnLoadingCallback? = null): ItemCoupon? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCouponDetail(uuid)
        }
    }

    suspend fun getUserBonus(auth: String, loadingCallback: OnLoadingCallback?): ItemUserBonus? {
        return getApiResult(loadingCallback) {
            getApiInterface().getUserBonus(auth)
        }
    }

    suspend fun getAccountBonus(auth: String, limit: Int, page: Int, loadingCallback: OnLoadingCallback?): ItemAccountBonus? {
        return getApiResult(loadingCallback) {
            getApiInterface().getAccountBonus(auth, limit, page)
        }
    }

    suspend fun getDistricts(auth: String, loadingCallback: OnLoadingCallback?): ItemDistricts? {
        return getApiResult(loadingCallback) {
            getApiInterface().getDistricts(auth)
        }
    }

    suspend fun getCouponDistricts(loadingCallback: OnLoadingCallback?): ItemCouponDistricts? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCouponDistricts()
        }
    }

    // TODO Note that if the parameters need to be changed.
    suspend fun getCouponStores(categoryId: Int, loadingCallback: OnLoadingCallback?): ItemCouponStores? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCouponStores(categoryId)
        }
    }
}