package com.example.fooddeliveryapp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.Activity.FoodDetailsActivity
import com.example.fooddeliveryapp.DataModel.CreatedRestaurants
import com.example.fooddeliveryapp.databinding.ViewRecommendedWideCardBinding

class RecommendedAdapter(
    private var restaurantList: List<CreatedRestaurants>,
    private val onAddClick: (CreatedRestaurants) -> Unit
): RecyclerView.Adapter<RecommendedAdapter.RecommendedViewHolder> () {

    inner class RecommendedViewHolder(val binding: ViewRecommendedWideCardBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedViewHolder {
        val binding = ViewRecommendedWideCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendedViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        val firstDish = restaurant.menu.firstOrNull()
        holder.binding.apply {
            foodName.text = firstDish?.name ?: "Dish name"
            price.text = "₹${firstDish?.price ?: "--"}"
            foodratings.text = "${firstDish?.ratings ?: "--"}"
            restaurantname.text = restaurant.name

            card.setOnClickListener {
                firstDish?._id.let { id ->
                    val intent = Intent(holder.itemView.context, FoodDetailsActivity::class.java)
                    intent.putExtra("MENU_ITEM_ID", id)
                    holder.itemView.context.startActivity(intent)
                }
            }

            Glide.with(ivThumb)
                .load(firstDish?.image ?: "https://via.placeholder.com/150")
                .into(ivThumb)

            btnAdd.setOnClickListener {
                onAddClick(restaurant)
            }
        }
    }

    override fun getItemCount(): Int = restaurantList.size

    fun updateList(newList: List<CreatedRestaurants>) {
        restaurantList = newList
        notifyDataSetChanged()
    }
}