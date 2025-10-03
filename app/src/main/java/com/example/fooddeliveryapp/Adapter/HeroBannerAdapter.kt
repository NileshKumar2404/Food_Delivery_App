package com.example.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.DataModel.featuredRestaurantsList
import com.example.fooddeliveryapp.databinding.ViewHeroBannerBinding

class HeroBannerAdapter(
    private var restaurants: List<featuredRestaurantsList>
): RecyclerView.Adapter<HeroBannerAdapter.HeroBannerViewHolder>()  {

    inner class HeroBannerViewHolder(val binding: ViewHeroBannerBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroBannerViewHolder {
        val binding = ViewHeroBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeroBannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroBannerViewHolder, position: Int) {
        val restaurant = restaurants[position]

        holder.binding.apply {
            Glide.with(ivHeroImage)
                .load(restaurant.image)
                .into(ivHeroImage)

            tvHeroTitle.text = restaurant.name
        }
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateItems(newRestaurants: List<featuredRestaurantsList>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}