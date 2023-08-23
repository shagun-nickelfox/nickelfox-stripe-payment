package com.example.paymentapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.paymentapp.databinding.ActivityChoosePaymentGatewayBinding
import com.example.paymentapp.ui.razorpay.RazorPayPaymentsActivity
import com.example.paymentapp.ui.stripe.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChoosePaymentGatewayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChoosePaymentGatewayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePaymentGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
    }

    private fun initListeners(){
        binding.apply {
            stripeBtn.setOnClickListener {
                startActivity(Intent(this@ChoosePaymentGatewayActivity,MainActivity::class.java))
            }

            razorPayBtn.setOnClickListener {
                startActivity(Intent(this@ChoosePaymentGatewayActivity,RazorPayPaymentsActivity::class.java))
            }
        }
    }
}