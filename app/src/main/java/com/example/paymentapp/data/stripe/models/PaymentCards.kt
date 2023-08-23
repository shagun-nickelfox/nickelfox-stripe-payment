package com.example.paymentapp.data.stripe.models

data class PaymentCards(
    val `data`: List<CardItems>,
    val has_more: Boolean,
    val `object`: String,
    val url: String
)
