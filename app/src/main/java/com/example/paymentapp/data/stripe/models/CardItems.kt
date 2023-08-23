package com.example.paymentapp.data.stripe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardItems(
    val address_city: String?,
    val address_country: String?,
    val address_line1: String?,
    val address_line1_check: String?,
    val address_line2: String?,
    val address_state: String?,
    val address_zip: String?,
    val address_zip_check: String?,
    val brand: String,
    val country: String,
    val customer: String,
    val cvc_check: String,
    val dynamic_last4: String?,
    val exp_month: Int,
    val exp_year: Int,
    val fingerprint: String,
    val funding: String,
    val id: String,
    val last4: String,
    val name: String,
    val `object`: String,
    val tokenization_method: String?,
    val wallet: String?
):Parcelable