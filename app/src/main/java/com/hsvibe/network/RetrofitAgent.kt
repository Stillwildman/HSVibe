package com.hsvibe.network

import com.hsvibe.utilities.L
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Vincent on 2021/06/28.
 */
object RetrofitAgent {

    private var retrofit: Retrofit? = null

    fun getRetrofit(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = newRetrofit(baseUrl)
        }
        else if (retrofit!!.baseUrl().toString() != baseUrl) {
            retrofit = newRetrofit(baseUrl)
        }
        return retrofit!!
    }

    private fun newRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(getOkHttpClient())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().run {
            val logInterceptor = HttpLoggingInterceptor { message ->
                L.d("RetrofitAgent", "HttpLog: $message")
            }
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            addInterceptor(logInterceptor)
            addInterceptor(BrotliInterceptor)

            addInterceptor(Interceptor { chain ->
                val headerBuilder = chain.request().newBuilder()

                headerBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")

                chain.proceed(headerBuilder.build())
            })

            build()
        }
    }

}