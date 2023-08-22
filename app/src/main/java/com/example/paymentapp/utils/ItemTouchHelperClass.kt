package com.example.paymentapp.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentapp.ui.stripe.CardsAdapter


class ItemTouchHelperClass(private val adapter: CardsAdapter): ItemTouchHelper.SimpleCallback(0,
    (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)){

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.currentList.getOrNull(viewHolder.bindingAdapterPosition)?.let{
            adapter.listener.onDelete(it.id,viewHolder.bindingAdapterPosition)
        }
    }
}