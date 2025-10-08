package com.example.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.MenuItemsContainer
import com.example.fooddeliveryapp.databinding.ViewRecommendedWideCardBinding

class MenuAdapter(
    private var menuList: List<MenuItemsContainer>,
    private val onAddClicked: (MenuItemsContainer) -> Unit
): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuViewHolder {
        val binding = ViewRecommendedWideCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MenuViewHolder,
        position: Int
    ) {
        val menu = menuList[position]
        holder.binding.apply {
            foodName.text = menu.name
            restaurantname.text = menu.restaurant.name
            foodratings.text = menu.ratings.toString()
            price.text = menu.price

            Glide.with(holder.itemView.context)
                .load(menu.image)
                .into(ivThumb)

            btnAdd.setOnClickListener {
                onAddClicked(menu)
            }
        }
    }

    override fun getItemCount(): Int = menuList.size

    inner class MenuViewHolder(val binding: ViewRecommendedWideCardBinding):
            RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: List<MenuItemsContainer>) {
        menuList = newList
        notifyDataSetChanged()
    }
}