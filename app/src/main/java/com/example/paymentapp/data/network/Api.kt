package com.example.paymentapp.data.network

import com.example.paymentapp.data.models.CardItems
import com.example.paymentapp.data.models.CustomerInfo
import com.example.paymentapp.data.models.DeleteCardResponse
import com.example.paymentapp.data.models.EphemeralKeyResponse
import com.example.paymentapp.data.models.PaymentCards
import com.example.paymentapp.data.models.PaymentIntent
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("/v1/customers")
    suspend fun getCustomerId(): Response<CustomerInfo>

    @FormUrlEncoded
    @POST("/v1/ephemeral_keys")
    suspend fun getEphemeralKey(@FieldMap body:Map<String,String>):Response<EphemeralKeyResponse>

    @FormUrlEncoded
    @POST("/v1/payment_intents")
    suspend fun getPaymentIntent(@FieldMap body: Map<String, @JvmSuppressWildcards Any>):Response<PaymentIntent>

    @FormUrlEncoded
    @POST("/v1/customers/{customerId}/sources")
    suspend fun addCard(@Path("customerId") customerId: String, @FieldMap body: Map<String,String>):
            Response<CardItems>

    @GET("/v1/customers/{customerId}/sources")
    suspend fun getCards(@Path("customerId") customerId: String):Response<PaymentCards>

    @FormUrlEncoded
    @POST("/v1/customers/{customerId}/sources/{cardId}")
    suspend fun updateCard(@Path("customerId") customerId: String,@Path("cardId") cardId:String,
                           @FieldMap body: Map<String,@JvmSuppressWildcards Any>):
            Response<CardItems>

    @DELETE("/v1/customers/{customerId}/sources/{cardId}")
    suspend fun deleteCard(@Path("customerId") customerId: String,@Path("cardId") cardId:String):
            Response<DeleteCardResponse>


}
