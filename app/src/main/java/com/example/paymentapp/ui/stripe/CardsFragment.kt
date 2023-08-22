package com.example.paymentapp.ui.stripe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.paymentapp.data.models.CardItems
import com.example.paymentapp.databinding.FragmentCardsBinding
import com.example.paymentapp.di.view_models.stripe.PaymentsViewModel
import com.example.paymentapp.di.view_models.stripe.PaymentsViewState
import com.example.paymentapp.utils.ItemTouchHelperClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsFragment : Fragment() {

    private lateinit var binding: FragmentCardsBinding
    private lateinit var viewModel: PaymentsViewModel
    private lateinit var adapter: CardsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PaymentsViewModel::class.java]
        viewModel.customerID?.let { viewModel.getCard() } ?: viewModel.getCustomerId(false)
        initRecyclerView()
        initObservers()
    }

    private fun initRecyclerView() {
        binding.apply {
            adapter = CardsAdapter(object : CardsAdapter.CardsAction {
                override fun onEdit(cardItems: CardItems) {
                    Log.d("CARD ID", "onEdit: id:${cardItems.id} ")
                    findNavController().navigate(
                        CardsFragmentDirections.actionToEditCardFragment(
                            cardItems
                        )
                    )
                }

                override fun onDelete(id: String, position: Int) {
                    viewModel.deleteCard(id, position)
                }

            })
            cardsRv.adapter = adapter
            val itemTouchHelperCallback = ItemTouchHelperClass(adapter)
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(cardsRv)
        }
    }

    private fun initObservers() {
        binding.apply {
            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                viewFlipper.displayedChild = when (state) {
                    is PaymentsViewState.Idle -> 0
                    is PaymentsViewState.Loading -> 1
                }
            }
            viewModel.customerIdSuccessEvent.observe(viewLifecycleOwner) {
                viewModel.getCard()
            }

            viewModel.cardListSuccessEvent.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            viewModel.cardDeleteSuccessEvent.observe(viewLifecycleOwner) {
                adapter.notifyItemRemoved(it)
            }
        }
    }
}