package com.example.paymentapp.data

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentPreferences @Inject constructor(@ApplicationContext context: Context)  {
    private val prefManager by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    var bearerToken: String?
        get() {
            val t = prefManager.getString(SECRET_TOKEN, null)
            return t
        }
        set(value) = with(prefManager.edit()) {
            putString(SECRET_TOKEN, value)
            apply()
        }

    var customerId: String?
        get() {
            val t = prefManager.getString(CUSTOMER_ID,null)
            return t
        }
        set(value) = with(prefManager.edit()){
            putString(CUSTOMER_ID,value)
            apply()
        }

    companion object{
        const val SECRET_TOKEN = "secret_token"
        const val CUSTOMER_ID = "customer_id"
    }
}