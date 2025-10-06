package com.example.blisscakes.DataClasses

data class CartItems(val id: Int,
                     val productName: String,
                     var quantity: Int,
                     val price: Double,
                     val image: Int
)