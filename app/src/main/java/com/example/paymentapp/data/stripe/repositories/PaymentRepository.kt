package com.example.paymentapp.data.stripe.repositories

import android.util.Log
import com.example.paymentapp.data.stripe.models.CardItems
import com.example.paymentapp.data.stripe.models.CustomerInfo
import com.example.paymentapp.data.stripe.models.DeleteCardResponse
import com.example.paymentapp.data.stripe.models.EphemeralKeyResponse
import com.example.paymentapp.data.stripe.models.PaymentIntent
import com.example.paymentapp.data.stripe.network.Api
import com.example.paymentapp.utils.ApiException
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

class PaymentRepository @Inject constructor(
    @Named("StripeApi") private val api: Api
) {
    suspend fun getCustomerId(): CustomerInfo? {
        val response = api.getCustomerId()
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }

    suspend fun getEphemeralKey(customerID: String): EphemeralKeyResponse? {
        Log.d("PAYMENTS EXCEPTION", "getEphemeralKey:customerID:$customerID ")
        val body = mapOf(
            "customer" to customerID)
        val response = api.getEphemeralKey(body)
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }

    suspend fun getClientSecret(
        customerID: String,
        amount:String,
        currency:String,
        automaticPaymentEnabled:Boolean): PaymentIntent?{
        val body = mapOf(
            "customer" to customerID,
            "amount" to amount+"00",
            "currency" to currency,
            "automatic_payment_methods[enabled]" to true
        )
        val response = api.getPaymentIntent(body)
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }

    suspend fun addCard(
        customerId: String,
        tokenId:String): CardItems?{
        val body = mapOf(
            "source" to tokenId
        )
        val response = api.addCard(customerId,body)
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }
    suspend fun getCard(
        customerId: String):List<CardItems>?{
        val response = api.getCards(customerId)
        if (response.isSuccessful)
            return response.body()?.data
        else {
            throw getApiException(response.errorBody())
        }
    }

    suspend fun updateCard(
        customerId: String,
        cardId: String,
        updatedList: Map<String, Any>
    ): CardItems? {
        val response = api.updateCard(customerId,cardId,updatedList)
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }

    suspend fun deleteCard(
        customerId: String,
        cardId:String): DeleteCardResponse? {
        val response = api.deleteCard(customerId,cardId)
        if (response.isSuccessful)
            return response.body()
        else {
            throw getApiException(response.errorBody())
        }
    }

    private fun getApiException(errorBody: ResponseBody?):Exception {
        val error = errorBody?.string()
        val errorObj = JSONObject(error ?: "{}")
        val errorMsg = errorObj.getJSONObject("error").getString("message")
        return ApiException(errorMsg)
    }
}