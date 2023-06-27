package com.example.paymentapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.paymentapp.R
import com.example.paymentapp.databinding.FragmentEditCardBinding
import com.example.paymentapp.di.view_models.PaymentsViewModel
import com.example.paymentapp.di.view_models.PaymentsViewState
import com.example.paymentapp.utils.Constants.EXPIRY_MONTH
import com.example.paymentapp.utils.Constants.EXPIRY_YEAR
import com.example.paymentapp.utils.Constants.NAME
import com.stripe.android.model.PaymentMethodCreateParams
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCardFragment : Fragment() {
    private lateinit var binding: FragmentEditCardBinding
    private val args: EditCardFragmentArgs by navArgs()
    private  lateinit var viewModel: PaymentsViewModel
    lateinit var cardUpdatedFieldList:MutableMap<String,Any>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PaymentsViewModel::class.java]
        binding.apply {
            setUpData()
            initListeners()
            initObservers()
        }
    }

    private fun setUpData() {
        cardUpdatedFieldList = mutableMapOf()
        binding.apply {
            args.let {
                val card = PaymentMethodCreateParams.Card(
                    null,
                    it.cardDetails.exp_month,
                    it.cardDetails.exp_year
                )
                cardInputWidget.populate(card)
                cardHolderNameEt.setText(args.cardDetails.name)
            }

            cardInputWidget.cardNumberTextInputLayout.isEnabled = false
            cardInputWidget.cvcInputLayout.isEnabled = false
            cardInputWidget.setCardNumber("************${args.cardDetails.last4}")
            cardInputWidget.cardNumberEditText.setTextColor(R.color.colorSecondaryText)
            cardInputWidget.cardNumberTextInputLayout.isErrorEnabled = false


        }
    }
    private fun initListeners() {
        binding.apply {
            cardInputWidget.setExpiryDateTextWatcher(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val month = s.toString().substringBefore("/")
                    val year = s.toString().substringAfter("/")
                    if(cardUpdatedFieldList.containsKey(EXPIRY_MONTH)) {
                        cardUpdatedFieldList.replace(EXPIRY_MONTH,month)
                        cardUpdatedFieldList.replace(EXPIRY_YEAR,year)
                    }
                    else {
                        cardUpdatedFieldList[EXPIRY_MONTH] = month
                        cardUpdatedFieldList[EXPIRY_YEAR] = year
                    }
                }
            })
            cardHolderNameEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,
                    start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if(cardUpdatedFieldList.containsKey(NAME))
                        cardUpdatedFieldList.replace(NAME,s.toString())
                    else
                        cardUpdatedFieldList[NAME] = s.toString()
                }
            })
            btnUpdate.setOnClickListener {
                if(cardUpdatedFieldList.isEmpty()) {
                    val body = mapOf<String,Any>(
                        EXPIRY_MONTH to args.cardDetails.exp_month,
                        EXPIRY_YEAR to args.cardDetails.exp_year,
                        NAME to args.cardDetails.name
                    )
                    viewModel.updateCard(args.cardDetails.id,body)
                }else
                    viewModel.updateCard(args.cardDetails.id,cardUpdatedFieldList)
            }

        }
    }

    private fun initObservers() {
        binding.apply {
            viewModel.viewState.observe(viewLifecycleOwner){ state ->
                viewFlipper.displayedChild = when(state){
                    is PaymentsViewState.Idle -> 0
                    is PaymentsViewState.Loading -> 1
                }
            }
            viewModel.cardActionSuccessEvent.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(),"Card Updated Successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}