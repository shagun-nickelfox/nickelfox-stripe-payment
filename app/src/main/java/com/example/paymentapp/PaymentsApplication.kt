package com.example.paymentapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaymentsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}