package com.example.paymentapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeleteCardResponse(
    val deleted: Boolean,
    val id: String,
    val `object`: String
):Parcelable
