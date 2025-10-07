package com.blisscakes.app.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blisscakes.app.R
import com.blisscakes.app.data.models.OrderItem
import com.blisscakes.app.databinding.ItemOrderItemBinding

class OrderItemAdapter(
    private val items: List<OrderItem>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OrderItemViewHolder(
        private val binding: ItemOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {
            binding.apply {
                tvCakeName.text = item.cake.name
                tvQuantity.text = "Qty: ${item.quantity}"
                tvPrice.text = "Rs. ${String.format("%.2f", item.price)}"
                tvSubtotal.text = "Rs. ${String.format("%.2f", item.subtotal)}"

                Glide.with(itemView.context)
                    .load(item.cake.imageUrl ?: item.cake.image)
                    .placeholder(R.drawable.placeholder_cake)
                    .error(R.drawable.placeholder_cake)
                    .centerCrop()
                    .into(ivCakeImage)
            }
        }
    }
}