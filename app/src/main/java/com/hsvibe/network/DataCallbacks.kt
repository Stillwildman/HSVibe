package com.hsvibe.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.hsvibe.callbacks.OnDataGetCallback
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Urls
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserToken
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.*
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
            L.d(TAG, "Suspend API Call! Thread is ${Thread.currentThread().name}")

            loadingCallback?.onLoadingStart()

            val response = makeApiCall()

            if (response.isSuccessful) {
                L.d(TAG, "API Response Successful!!!")
                loadingCallback?.onLoadingEnd()
                response.body()
            }
            else {
                L.e(TAG, "API Response error!!!")
                val errorBody = response.errorBody()?.charStream()?.readText()

                val messageItem = try {
                    Gson().fromJson(errorBody, ItemMessage::class.java)
                }
                catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    null
                }

                loadingCallback?.onLoadingFailed(errorBody)
                loadingCallback?.onLoadingEnd()
                throw ApiStatusException(response.code(), errorBody, messageItem)
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

    suspend fun getCoupon(orderBy: String = "${ApiConst.ORDER_BY_TOP};${ApiConst.ORDER_BY_UPDATED}",
                          sortedBy: String = "${ApiConst.SORTED_BY_DESC};${ApiConst.SORTED_BY_DESC}",
                          storeIds: String? = null,
                          limit: Int = ApiConst.DEFAULT_LIMIT,
                          page: Int = 1,
                          loadingCallback: OnLoadingCallback? = null
    ): ItemCoupon? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCoupon(orderBy, sortedBy, storeIds, limit, page)
        }
    }

    suspend fun redeemCoupon(auth: String, uuid: String, loadingCallback: OnLoadingCallback?): ItemMessage? {
        return getApiResult(loadingCallback) {
            getApiInterface().redeemCoupon(auth, uuid)
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

    suspend fun getCouponBrands(categoryId: Int? = null, partnerId: Int? = null, loadingCallback: OnLoadingCallback?): ItemBrand? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCouponBrands(categoryId, partnerId)
        }
    }

    suspend fun getMyCouponList(auth: String, loadingCallback: OnLoadingCallback?): ItemMyCoupon? {
        return getApiResult(loadingCallback) {
            getApiInterface().getMyCouponList(auth)
        }
    }

    suspend fun useCoupon(auth: String, couponUseItem: PostUseCoupon, loadingCallback: OnLoadingCallback?): ItemPayloadCode? {
        return getApiResult(loadingCallback) {
            getApiInterface().useCoupon(auth, couponUseItem)
        }
    }

    suspend fun verifyPayPassword(auth: String, passwordItem: PostPassword, loadingCallback: OnLoadingCallback?): ItemMessage? {
        return getApiResult(loadingCallback) {
            getApiInterface().verifyPayPassword(auth, passwordItem)
        }
    }

    suspend fun getCreditCards(auth: String, loadingCallback: OnLoadingCallback?): ItemCardList? {
        return getApiResult(loadingCallback) {
            getApiInterface().getCreditCards(auth)
        }
    }

    suspend fun deleteCreditCard(auth: String, key: String, loadingCallback: OnLoadingCallback?): ItemCardList? {
        return getApiResult(loadingCallback) {
            getApiInterface().deleteCreditCard(auth, key)
        }
    }

    suspend fun getPaymentCode(auth: String, payloadItem: PostPaymentPayload, loadingCallback: OnLoadingCallback?): ItemPayloadCode? {
        return getApiResult(loadingCallback) {
            getApiInterface().getPaymentCode(auth, payloadItem)
        }
    }

    suspend fun getTransactionHistory(auth: String,
                                      orderBy: String = "${ApiConst.ORDER_BY_TOP};${ApiConst.ORDER_BY_UPDATED}",
                                      sortedBy: String = "${ApiConst.SORTED_BY_DESC};${ApiConst.SORTED_BY_DESC}",
                                      limit: Int = ApiConst.DEFAULT_LIMIT,
                                      page: Int = 1,
                                      loadingCallback: OnLoadingCallback? = null
    ): ItemTransactions? {
        return getApiResult(loadingCallback) {
            getApiInterface().getTransactionHistory(auth, orderBy, sortedBy, limit, page)
        }
    }

    suspend fun transferPoint(auth: String, transferItem: PostTransferPoint, loadingCallback: OnLoadingCallback?): ItemPointTransfer? {
        return getApiResult(loadingCallback) {
            getApiInterface().transferPoint(auth, transferItem)
        }
    }
}