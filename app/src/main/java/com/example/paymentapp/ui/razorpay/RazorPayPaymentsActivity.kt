package com.example.paymentapp.ui.razorpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.doAfterTextChanged
import com.example.paymentapp.databinding.ActivityRazorPayPaymentsBinding
import com.example.paymentapp.utils.Constants
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class RazorPayPaymentsActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityRazorPayPaymentsBinding
    private lateinit var checkout: Checkout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRazorPayPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
        This Checkout.preload function preload necessary resources and configurations
        before initiating the payment process with Razorpay.
         */
        Checkout.preload(applicationContext)
        checkout = Checkout()
        checkout.setKeyID(Constants.KEY_ID)
        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            amountET.doAfterTextChanged {
                binding.payNowBtn.isEnabled = !binding.amountET.text.isNullOrEmpty()
            }
            payNowBtn.setOnClickListener {
                savePaymentsUsingJSON(amountET.text.toString().trim().toInt())
            }
        }
    }

    private fun savePaymentsUsingJSON(amount: Int) {
        try {
            val amountObj = JSONObject()
            amountObj.put("name", "Razorpay Demo")
            amountObj.put("amount", amount *100)
            amountObj.put("currency", "INR")

            checkout.open(this, amountObj)
        } catch (e: Exception) {
            Log.d("RAZORPAY PAYMENTS", "savePayments:error: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        try{
            startActivity(Intent(this,CardsActivity::class.java))
        }catch(e:Exception){
            Log.d("RAZORPAY PAYMENT", "onPaymentSuccess:${e.localizedMessage} ")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Log.d("RAZORPAY PAYMENT", "onPaymentError:$p1 ")
    }
}