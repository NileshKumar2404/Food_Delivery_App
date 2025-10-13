package com.example.fooddeliveryapp.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.DataModel.SavedAddress
import com.example.fooddeliveryapp.databinding.ItemSelectAddressBinding


class SelectedAddressAdapter(
    private var addresses: List<SavedAddress> = emptyList(),
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<SelectedAddressAdapter.AddressViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    fun submitList(list: List<SavedAddress>) {
        addresses = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemSelectAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val address = addresses[position]
        holder.binding.apply {
            tvAddressLabel.text = address.label ?: "Home"
            tvAddressDetails.text = "${address.street}, ${address.city}, ${address.state} - ${address.pinCode}"

            // Highlight selected card visually (optional)
            root.alpha = if (selectedPosition == position) 0.8f else 1f

            root.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onSelect(address._id) // Send selected ID back
            }
        }
    }

    override fun getItemCount(): Int = addresses.size

    inner class AddressViewHolder(val binding: ItemSelectAddressBinding) :
        RecyclerView.ViewHolder(binding.root)
}