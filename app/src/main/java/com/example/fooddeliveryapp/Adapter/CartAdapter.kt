package com.example.fooddeliveryapp.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.CartItems
import com.example.fooddeliveryapp.databinding.ItemCartBinding

class CartAdapter (
    private var cartItems: MutableList<CartItems>,
    private val onQuantityChanged: (CartItems, Int) -> Unit,
    private val onDeleteItem: (CartItems, Int) -> Unit
): RecyclerView.Adapter<CartAdapter.CartViewHolder> (){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val items = cartItems[position]

        Log.e("CartAdapter", "Binding: ${items.menuItem.name}, qty=${items.quantity}")

        holder.binding.apply {
            tvFoodName.text = items.menuItem.name
            tvCartFoodPrice.text = "₹${items.menuItem.price.replace(Regex("[^0-9]"), "")}"
            tvCartQty.text = items.quantity.toString()

            Glide.with(holder.itemView.context)
                .load(items.menuItem.image)
                .into(ivCartImage)

            btnCartPlus.setOnClickListener {
                val newQty = items.quantity + 1
                onQuantityChanged(items, newQty)
            }

            btnCartMinus.setOnClickListener {
                if (items.quantity > 1){
                    val newQty = items.quantity - 1
                    onQuantityChanged(items, newQty)
                }
            }

            btnCartDelete.setOnClickListener {
                onDeleteItem(items, position)
            }
        }
    }

    override fun getItemCount() = cartItems.size

    inner class CartViewHolder(val binding: ItemCartBinding): RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: List<CartItems>){
        cartItems.clear()
        cartItems.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateQuantity(position: Int, newQty: Int) {
        cartItems[position] = cartItems[position].copy(quantity = newQty)
        notifyItemChanged(position)
    }
}