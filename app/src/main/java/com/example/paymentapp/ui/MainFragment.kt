package com.example.paymentapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.paymentapp.databinding.FragmentMainBinding
import com.example.paymentapp.utils.Constants
import com.example.paymentapp.utils.readCustomerId
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    lateinit var customerId: String
    var amount: String? = null
    lateinit var ephericalKey: String
    lateinit var clientSecret: String
    lateinit var paymentSheet: PaymentSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PaymentConfiguration.init(requireActivity(), Constants.publish_key)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        binding.btnStripePay.setOnClickListener {
            if (requireActivity().readCustomerId() != null) {
                getEphericalKey()
            } else {
                getCustomerId()
                getEphericalKey()
            }
        }

        binding.btnAddCard.setOnClickListener {
            if (requireActivity().readCustomerId() != null)
                findNavController().navigate(MainFragmentDirections.actionAddCardFragment())
            else {
                getCustomerId()
                findNavController().navigate(MainFragmentDirections.actionAddCardFragment())
            }
        }

        binding.etAmount.doAfterTextChanged {
            binding.btnStripePay.isEnabled = !binding.etAmount.text.isNullOrEmpty()
            amount = binding.etAmount.text.toString()
        }
    }

    private fun getCustomerId() {
        val queue = Volley.newRequestQueue(requireActivity())
        val stringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/customers",
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    customerId = jsonObject.getString("id")
                    saveCustomerId(customerId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${Constants.secret_key}"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    private fun getEphericalKey() {
        val queue = Volley.newRequestQueue(requireActivity())
        val stringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/ephemeral_keys",
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    ephericalKey = jsonObject.getString("secret")
                    getClientSecret()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${Constants.secret_key}"
                headers["Stripe-Version"] = "2022-11-15"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = requireContext().readCustomerId().toString()
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun getClientSecret() {
        val queue = Volley.newRequestQueue(requireActivity())
        val stringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/payment_intents",
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    clientSecret = jsonObject.getString("client_secret")
                    paymentFlow()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${Constants.secret_key}"
                return headers
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["customer"] = requireActivity().readCustomerId().toString()
                params["amount"] = amount!! + "00"
                params["currency"] = "usd"
                params["automatic_payment_methods[enabled"] = "true"
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecret, PaymentSheet.Configuration(
                "ABC Company", requireActivity().readCustomerId()?.let {
                    PaymentSheet.CustomerConfiguration(
                        it, ephericalKey
                    )
                }
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(requireActivity(), "Payment Success", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Canceled -> {
                Toast.makeText(requireActivity(), "Payment Canceled", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Failed -> {
                Toast.makeText(requireActivity(), "Payment Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveCustomerId(customerId: String) {
        val sharedPref =
            requireActivity().getSharedPreferences("application", ComponentActivity.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("CUSTOMER_ID", customerId)
        editor.apply()
    }
}