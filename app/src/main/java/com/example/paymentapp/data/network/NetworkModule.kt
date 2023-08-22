package com.example.paymentapp.data.network

import com.example.paymentapp.BuildConfig
import com.example.paymentapp.utils.Constants.AUTHORIZATION
import com.example.paymentapp.utils.Constants.BEARER
import com.example.paymentapp.utils.Constants.NETWORK_TIMEOUT
import com.example.paymentapp.utils.Constants.STRIPE_VERSION
import com.example.paymentapp.utils.Constants.VERSION_NO
import com.example.paymentapp.utils.Constants.secret_key
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideTokenInterceptor() = Interceptor { chain ->
        val newRequest = chain.request().newBuilder()
            .addHeader(AUTHORIZATION, BEARER + secret_key)
            .addHeader(STRIPE_VERSION, VERSION_NO)
            .build()
        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: Interceptor,
    ): OkHttpClient {
        val client = OkHttpClient.Builder().readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS).addInterceptor(tokenInterceptor)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(logging)
        }

        return client.build()
    }

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient): Api {
        val builder =
            Retrofit.Builder().baseUrl("https://api.stripe.com")
                .addConverterFactory(GsonConverterFactory.create())
        builder.client(client)
        return builder.build().create(Api::class.java)
    }
}