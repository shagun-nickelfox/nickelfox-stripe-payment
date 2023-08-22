package com.example.paymentapp.di.view_models.stripe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paymentapp.data.PaymentPreferences
import com.example.paymentapp.data.models.CardItems
import com.example.paymentapp.data.models.PaymentIntent
import com.example.paymentapp.data.repositories.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val preferences: PaymentPreferences
) : ViewModel() {

    val customerID get() = preferences.customerId
    var amount: String? = null
    private var currency: String = "inr"
    lateinit var ephemeralKey: String
    lateinit var clientSecret: String
    lateinit var cardId: String
    val paymentIntentSuccessEvent = MutableLiveData<PaymentIntent>()
    val customerIdSuccessEvent = MutableLiveData<Unit>()
    val cardActionSuccessEvent = MutableLiveData<Unit>()
    val cardDeleteSuccessEvent = MutableLiveData<Int>()
    val cardListSuccessEvent = MutableLiveData<List<CardItems>?>()
    private val _viewState = MutableLiveData<PaymentsViewState>(PaymentsViewState.Idle)
    val viewState: LiveData<PaymentsViewState> = _viewState
    val errorEvent = MutableLiveData<String>()

    fun getCustomerId(getEphemeralKey: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _viewState.postValue(PaymentsViewState.Loading)
            val result = repository.getCustomerId()
            preferences.customerId = result?.id
            if (result != null) {
                if (getEphemeralKey) {
                    getEphemeralKey(false)
                }
                else {
                    _viewState.postValue(PaymentsViewState.Idle)
                    customerIdSuccessEvent.postValue(Unit)
                }
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "getCustomerId: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    fun getEphemeralKey(showState:Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if(showState)
                _viewState.postValue(PaymentsViewState.Loading)
            val result = customerID?.let { repository.getEphemeralKey(it) }
            if (result != null)
                ephemeralKey = result.secret
            createPaymentIntent()
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "getEphemeralKey: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    private fun createPaymentIntent() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = customerID?.let {
                repository.getClientSecret(it, amount ?: "00", currency, true)
            }
            result?.let {
                _viewState.postValue(PaymentsViewState.Idle)
                clientSecret = it.client_secret
                paymentIntentSuccessEvent.postValue(it)
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "getPaymentIntent: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    fun addCard(tokenId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _viewState.postValue(PaymentsViewState.Loading)
            val result = customerID?.let { repository.addCard(it, tokenId) }
            if (result != null) {
                _viewState.postValue(PaymentsViewState.Idle)
                cardId = result.id
                cardActionSuccessEvent.postValue(Unit)
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "addCard: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    fun getCard() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _viewState.postValue(PaymentsViewState.Loading)
            val result = customerID?.let { repository.getCard(it) }
            if (result != null) {
                _viewState.postValue(PaymentsViewState.Idle)
                cardListSuccessEvent.postValue(result)
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "getCard: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }

    fun updateCard(cardId: String,updatedList:Map<String,Any>) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _viewState.postValue(PaymentsViewState.Loading)
            val result = customerID?.let { repository.updateCard(it, cardId,updatedList) }
            if (result != null) {
                _viewState.postValue(PaymentsViewState.Idle)
                cardActionSuccessEvent.postValue(Unit)
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "updateCard: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }
    fun deleteCard(cardId: String,position:Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _viewState.postValue(PaymentsViewState.Loading)
            val result = customerID?.let { repository.deleteCard(it, cardId) }
            if (result != null) {
                _viewState.postValue(PaymentsViewState.Idle)
                cardDeleteSuccessEvent.postValue(position)
            }
        } catch (e: Exception) {
            _viewState.postValue(PaymentsViewState.Idle)
            errorEvent.postValue(e.localizedMessage)
            Log.d("PAYMENTS EXCEPTION", "deleteCard: ${e.localizedMessage} ")
            e.printStackTrace()
        }
    }
}

sealed class PaymentsViewState{
    object Idle : PaymentsViewState()
    object Loading : PaymentsViewState()
}