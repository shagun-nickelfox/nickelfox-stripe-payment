package com.example.paymentapp.data.stripe.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteCardResponse(
    val deleted: Boolean,
    val id: String,
    val `object`: String
):Parcelable
