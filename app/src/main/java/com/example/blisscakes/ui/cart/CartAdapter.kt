package com.blisscakes.app.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blisscakes.app.R
import com.blisscakes.app.data.models.CartItem
import com.blisscakes.app.databinding.ItemCartBinding

class CartAdapter(
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onRemoveItem: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.apply {
                tvCakeName.text = item.cake.name
                tvCakePrice.text = "Rs. ${String.format("%.2f", item.price)}"
                tvQuantity.text = item.quantity.toString()
                tvSubtotal.text = "Rs. ${String.format("%.2f", item.subtotal)}"

                Glide.with(itemView.context)
                    .load(item.cake.imageUrl ?: item.cake.image)
                    .placeholder(R.drawable.placeholder_cake)
                    .error(R.drawable.placeholder_cake)
                    .centerCrop()
                    .into(ivCakeImage)

                btnIncrease.setOnClickListener {
                    if (item.quantity < 10) {
                        onQuantityChanged(item.id, item.quantity + 1)
                    }
                }

                btnDecrease.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChanged(item.id, item.quantity - 1)
                    }
                }

                btnRemove.setOnClickListener {
                    onRemoveItem(item.id)
                }
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}