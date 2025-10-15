package com.example.fooddeliveryapp.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.OrderData
import com.example.fooddeliveryapp.databinding.ItemsMyOrdersBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MyOrderAdapter(
    private var orders: List<OrderData> = emptyList()
): RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyOrderViewHolder {
        val binding = ItemsMyOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyOrderViewHolder,
        position: Int
    ) {
        val orderList = orders[position]

        holder.binding.apply {
            tvRestaurantName.text = orderList.restaurantDetails.name
            tvCuisineType.text = "Cuisine: ${orderList.restaurantDetails.cuisine.joinToString( ", " )}"
            tvTotalPrice.text = "₹${orderList.totalPrice}"

            tvPaymentMethod.text = "Payment: ${orderList.payment.method}"
            tvOrderStatus.text = "Delivery Status: ${orderList.status}"

            tvOrderDate.text = formatDate(orderList.createdAt)

            Glide.with(holder.itemView.context)
                .load(orderList.restaurantDetails.image)
                .into(ivRestaurantImage)

            val statusColor = when (orderList.status.lowercase(Locale.ROOT)) {
                "pending" -> "#FFA726"
                "delivered" -> "#43A047"
                "cancelled" -> "#E53935"
                else -> "#757575"
            }

            tvOrderStatus.setBackgroundColor(Color.parseColor(statusColor))
        }
    }

    private fun formatDate(date: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(date)
            val formatter = SimpleDateFormat("dd-MM-yyyy, hh:mm a", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            "Unknown Date"
        }
    }

    override fun getItemCount(): Int = orders.size

    inner class MyOrderViewHolder(val binding: ItemsMyOrdersBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newList: List<OrderData>) {
        orders = newList
        notifyDataSetChanged()
    }
}