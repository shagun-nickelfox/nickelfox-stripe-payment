package com.example.paymentapp.ui.razorpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paymentapp.R
import com.example.paymentapp.databinding.ActivityCardsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_cards)
    }
}