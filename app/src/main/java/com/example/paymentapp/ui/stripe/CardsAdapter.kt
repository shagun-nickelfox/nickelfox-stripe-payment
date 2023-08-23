package com.example.paymentapp.ui.stripe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paymentapp.data.stripe.models.CardItems
import com.example.paymentapp.databinding.CardItemsLayoutBinding


class CardsAdapter(val listener: CardsAction): ListAdapter<CardItems, CardsAdapter.CardsViewHolder>(
    CardsDifUtil()
) {

    inner class CardsViewHolder(private val binding: CardItemsLayoutBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(cardItems: CardItems){
            binding.apply {
                cardHolderNameEt.text = cardItems.name
                cardTypeTv.text = cardItems.brand
                countryTv.text = cardItems.country
                accountNumberTv.text = buildString {
                    append("************${cardItems.last4}") }
                expiryDate.text = buildString {
                    append("${cardItems.exp_month} / ${cardItems.exp_year}")
                }
                itemView.rootView.setOnClickListener {
                    listener.onEdit(cardItems)
                }
            }
        }
    }
    class CardsDifUtil: DiffUtil.ItemCallback<CardItems>(){
        override fun areItemsTheSame(oldItem: CardItems, newItem: CardItems): Boolean {
            return oldItem.id ==newItem.id
        }

        override fun areContentsTheSame(oldItem: CardItems, newItem: CardItems): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder =
        CardsViewHolder(CardItemsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) = holder.bind(getItem(position))

    interface CardsAction {
        fun onEdit(cardItems: CardItems)
        fun onDelete(id: String,position: Int)
    }
}