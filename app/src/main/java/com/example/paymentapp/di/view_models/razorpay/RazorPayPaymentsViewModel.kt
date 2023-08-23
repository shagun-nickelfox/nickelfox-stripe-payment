package com.example.paymentapp.di.view_models.razorpay

import androidx.lifecycle.ViewModel
import com.example.paymentapp.data.PaymentPreferences
import com.example.paymentapp.data.razorpay.repositories.RazorPayPaymentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RazorPayPaymentsViewModel @Inject constructor(
    private val repository: RazorPayPaymentsRepository,
    private val preferences: PaymentPreferences
) : ViewModel() {

}