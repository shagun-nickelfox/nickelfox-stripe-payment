package com.example.paymentapp.utils

import android.content.Context
import androidx.activity.ComponentActivity

fun Context.readCustomerId(): String? {
    val sharedPref = getSharedPreferences("application", ComponentActivity.MODE_PRIVATE)
    return sharedPref.getString("CUSTOMER_ID", null)
}