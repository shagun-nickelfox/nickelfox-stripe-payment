package com.example.paymentapp.utils

open class LocalizedException(message:String): Exception(message)

class ApiException(message:String): LocalizedException(message)