package com.example.paymentapp.ui.stripe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.paymentapp.data.models.PaymentIntent
import com.example.paymentapp.databinding.FragmentMainBinding
import com.example.paymentapp.di.view_models.stripe.PaymentsViewModel
import com.example.paymentapp.utils.Constants
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var paymentSheet: PaymentSheet
    private  lateinit var viewModel: PaymentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PaymentsViewModel::class.java]
        PaymentConfiguration.init(requireActivity(), Constants.publish_key)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.apply {
            etAmount.doAfterTextChanged {
                binding.btnStripePay.isEnabled = !binding.etAmount.text.isNullOrEmpty()
                viewModel.amount = binding.etAmount.text.toString()
            }
            btnStripePay.setOnClickListener {
                if(viewModel.customerID != null)
                    viewModel.getEphemeralKey()
                else
                    viewModel.getCustomerId()
            }
            btnAddCard.setOnClickListener {
                findNavController().navigate(MainFragmentDirections.actionAddCardFragment())
            }
            btnShowCard.setOnClickListener {
               findNavController().navigate(MainFragmentDirections.actionToCardsFragment())
            }
        }
    }

    private fun initObservers() {
        viewModel.paymentIntentSuccessEvent.observe(viewLifecycleOwner){
            paymentFlow(it)
        }
    }
    private fun paymentFlow(it: PaymentIntent?) {
        paymentSheet.presentWithPaymentIntent(viewModel.clientSecret,PaymentSheet.Configuration(
            "ABC COMPANY",
            viewModel.customerID?.let { it1 -> PaymentSheet.CustomerConfiguration(it1,viewModel.ephemeralKey) }
        ))

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
}