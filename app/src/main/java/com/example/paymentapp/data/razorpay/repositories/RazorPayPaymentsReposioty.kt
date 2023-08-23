package com.example.paymentapp.data.razorpay.repositories

import com.example.paymentapp.data.razorpay.razorpaynetwork.RazorPayApi
import javax.inject.Inject
import javax.inject.Named

class RazorPayPaymentsRepository @Inject constructor(@Named("RazorpayApi") private val api:RazorPayApi) {
}