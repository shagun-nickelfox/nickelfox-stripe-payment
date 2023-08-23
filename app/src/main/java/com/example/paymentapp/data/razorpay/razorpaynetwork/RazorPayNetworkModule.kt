package com.example.paymentapp.data.razorpay.razorpaynetwork

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RazorPayNetworkModule {

    @Singleton
    @Provides
    @Named("RazorpayInterceptor")
    fun provideTokenInterceptor() = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
        chain.proceed(newRequest.build())
    }

    @Singleton
    @Provides
    @Named("RazorpayClient")
    fun provideOkHttpClient(@Named("RazorpayInterceptor") tokenInterceptor: Interceptor): OkHttpClient {
        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
        return client.build()
    }

    @Singleton
    @Provides
    @Named("RazorpayApi")
    fun provideRetrofitApi(@Named("RazorpayClient") client: OkHttpClient):RazorPayApi{
        val builder = Retrofit.Builder()
            .baseUrl("https://api.razorpay.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
        builder.client(client)
        return builder.build().create(RazorPayApi::class.java)
    }
}