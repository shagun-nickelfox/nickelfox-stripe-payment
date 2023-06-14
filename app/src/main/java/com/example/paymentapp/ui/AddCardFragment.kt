package com.example.paymentapp.ui

import android.os.Bundle
import android.os.StrictMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.paymentapp.R
import com.example.paymentapp.databinding.FragmentAddCardBinding
import com.example.paymentapp.utils.Constants
import com.example.paymentapp.utils.readCustomerId
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.PaymentMethod

class AddCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCardBinding
    private var cardDetails = hashMapOf<String, Any>()
    private var billingDetails = hashMapOf<String, Any>()
    private var allDetails = hashMapOf<String, Any>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Stripe.apiKey = Constants.secret_key
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        initListeners()
    }

    private fun initListeners() {
        binding.apply {
            etCardNumber.doAfterTextChanged {
                setButtonEnableDisable()
            }

            etCardNumber.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && etCardNumber.text.isNullOrEmpty())
                    etCardNumber.error = getString(R.string.card_number_error)
            }

            etCardHolderName.doAfterTextChanged {
                setButtonEnableDisable()
            }

            etCardHolderName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && etCardHolderName.text.isNullOrEmpty())
                    etCardHolderName.error = getString(R.string.card_holder_error)
            }


            etCardExpiryMonth.doAfterTextChanged {
                setButtonEnableDisable()
            }

            etCardExpiryMonth.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && etCardExpiryMonth.text.isNullOrEmpty())
                    etCardExpiryMonth.error = getString(R.string.card_expire_month_error)
            }

            etCardExpiryYear.doAfterTextChanged {
                setButtonEnableDisable()
            }

            etCardExpiryYear.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && etCardExpiryYear.text.isNullOrEmpty())
                    etCardExpiryYear.error = getString(R.string.card_expire_year_error)
            }

            etCardCVV.doAfterTextChanged {
                setButtonEnableDisable()
            }

            etCardCVV.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && etCardCVV.text.isNullOrEmpty())
                    etCardCVV.error = getString(R.string.card_cvv_error)
            }

            btnSave.setOnClickListener {
                cardDetails["number"] = etCardNumber.text.toString()
                cardDetails["exp_month"] = etCardExpiryMonth.text.toString()
                cardDetails["exp_year"] = etCardExpiryYear.text.toString()
                cardDetails["cvc"] = etCardCVV.text.toString()
                billingDetails["name"] = etCardHolderName.text.toString()
                allDetails["type"] = "card"
                allDetails["card"] = cardDetails
                allDetails["billing_details"] = billingDetails

                addCardToStripe(allDetails)
            }
        }
    }

    private fun addCardToStripe(allDetails: HashMap<String, Any>) {
        try {
            val paymentMethod = PaymentMethod.create(allDetails)
            PaymentMethod.retrieve(paymentMethod.id)
                .attach(mapOf(Pair("customer", requireActivity().readCustomerId())))
            Toast.makeText(requireContext(), "Card added Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: StripeException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setButtonEnableDisable() {
        binding.apply {
            btnSave.isEnabled = when {
                etCardNumber.text.isNullOrEmpty() -> false
                etCardHolderName.text.isNullOrEmpty() -> false
                etCardExpiryMonth.text.isNullOrEmpty() -> false
                etCardExpiryYear.text.isNullOrEmpty() -> false
                etCardCVV.text.isNullOrEmpty() -> false
                else -> true
            }
        }
    }
}