package com.example.paymentapp.data.stripe.models

data class AutomaticPaymentMethods(
    val allow_redirects: String,
    val enabled: Boolean
)