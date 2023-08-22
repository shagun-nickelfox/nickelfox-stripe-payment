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
import com.example.paymentapp.databinding.FragmentAddCardBinding
import com.example.paymentapp.di.view_models.stripe.PaymentsViewModel
import com.example.paymentapp.di.view_models.stripe.PaymentsViewState
import com.example.paymentapp.utils.Constants
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCardFragment : Fragment() {
    private lateinit var binding: FragmentAddCardBinding
    private lateinit var viewModel: PaymentsViewModel
    private lateinit var tokenId :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddCardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PaymentsViewModel::class.java]
        PaymentConfiguration.init(
            requireContext(), Constants.publish_key
        )
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.apply {
           cardHolderNameEt.doAfterTextChanged {
               btnAdd.isEnabled = !cardHolderNameEt.text.isNullOrEmpty()
           }
            btnAdd.setOnClickListener {
                val cardWidgetParams = cardInputWidget.cardParams
                cardWidgetParams?.name = cardHolderNameEt.text.toString()
                if (cardWidgetParams != null) {
                    createToken(cardWidgetParams)
                }
            }
        }
    }
    private fun initObservers() {
        binding.apply {
            viewModel.viewState.observe(viewLifecycleOwner){ state ->
                when(state){
                    is PaymentsViewState.Idle -> viewFlipper.displayedChild = 0
                    is PaymentsViewState.Loading -> viewFlipper.displayedChild = 1
                }
            }
            viewModel.customerIdSuccessEvent.observe(viewLifecycleOwner){
                addCardToCustomer()
            }
            viewModel.cardActionSuccessEvent.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(),"Card Added Successfully",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            viewModel.errorEvent.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createToken(cardWidgetParams: CardParams) {
        val stripe = com.stripe.android.Stripe(requireContext(), Constants.publish_key)
        stripe.createCardToken(cardWidgetParams, callback = object : ApiResultCallback<Token> {
            override fun onError(e: Exception) {
                Toast.makeText(requireContext(), "Error creating token", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: Token) {
                tokenId = result.id
                if (viewModel.customerID != null)
                    addCardToCustomer()
                else
                    viewModel.getCustomerId(false)
            }

        })
    }

    private fun addCardToCustomer() {
        viewModel.addCard(tokenId)
    }
}