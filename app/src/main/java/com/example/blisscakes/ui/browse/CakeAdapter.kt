package com.blisscakes.app.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.blisscakes.app.R
import com.blisscakes.app.data.models.Cake
import com.blisscakes.app.databinding.ItemCakeBinding

class CakeAdapter(
    private val onCakeClick: (Cake) -> Unit
) : ListAdapter<Cake, CakeAdapter.CakeViewHolder>(CakeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CakeViewHolder {
        val binding = ItemCakeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CakeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CakeViewHolder(
        private val binding: ItemCakeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cake: Cake) {
            binding.apply {
                tvCakeName.text = cake.name
                tvCakePrice.text = cake.formattedPrice ?: "Rs. ${String.format("%.2f", cake.price)}"
                tvCakeSize.text = cake.size
                tvRating.text = String.format("%.1f ⭐ (%d)", cake.averageRating, cake.totalReviews)

                // Load image with Glide
                Glide.with(itemView.context)
                    .load(cake.imageUrl ?: cake.image)
                    .placeholder(R.drawable.placeholder_cake)
                    .error(R.drawable.placeholder_cake)
                    .centerCrop()
                    .into(ivCakeImage)

                root.setOnClickListener {
                    onCakeClick(cake)
                }
            }
        }
    }

    private class CakeDiffCallback : DiffUtil.ItemCallback<Cake>() {
        override fun areItemsTheSame(oldItem: Cake, newItem: Cake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cake, newItem: Cake): Boolean {
            return oldItem == newItem
        }
    }
}

