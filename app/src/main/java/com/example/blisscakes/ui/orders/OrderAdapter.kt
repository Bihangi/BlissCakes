package com.blisscakes.app.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blisscakes.app.R
import com.blisscakes.app.data.models.Order
import com.blisscakes.app.databinding.ItemOrderBinding

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderNumber.text = "Order #${order.orderNumber}"
                tvOrderDate.text = order.createdAt
                tvOrderStatus.text = order.status.uppercase()
                tvOrderTotal.text = order.formattedTotal ?: "Rs. ${String.format("%.2f", order.totalAmount)}"
                tvItemCount.text = "${order.items.size} items"

                // Set status color
                val statusColor = when (order.status) {
                    "pending" -> R.color.status_pending
                    "confirmed" -> R.color.status_confirmed
                    "preparing" -> R.color.status_preparing
                    "delivered" -> R.color.status_delivered
                    "cancelled" -> R.color.status_cancelled
                    else -> R.color.status_default
                }

                tvOrderStatus.setTextColor(
                    ContextCompat.getColor(itemView.context, statusColor)
                )

                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}