package com.example.fooddeliveryapp.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.Activity.AddressActivity
import com.example.fooddeliveryapp.DataModel.SavedAddress
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.ItemSavedAddressesBinding

class SavedAddressAdapter(
    private val addressList: MutableList<SavedAddress>,
    private val onDeleteClicked: (SavedAddress, Int) -> Unit,
): RecyclerView.Adapter<SavedAddressAdapter.SavedAddressViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedAddressViewHolder {
        val binding = ItemSavedAddressesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedAddressViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SavedAddressViewHolder,
        position: Int
    ) {
        val address = addressList[position]
        val addressText = "${address.street}, ${address.city}, ${address.state}, ${address.pinCode}"

        if (address.label == "Home") {
            holder.binding.ivType.setImageResource(R.drawable.home)
        } else {
            holder.binding.ivType.setImageResource(R.drawable.work)
        }

        holder.binding.apply {
            tvName.text = address.name
            tvAddress.text = addressText
            tvPhone.text = address.phone

            btnMore.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.item_saved_address_menu, popupMenu.menu)

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popupMenu)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_edit -> {
                            val context = view.context
                            val intent = Intent(context, AddressActivity::class.java)
                            intent.putExtra("IS_EDIT_MODE", true)
                            intent.putExtra("ADDRESS_ID", address._id)
                            intent.putExtra("NAME", address.name)
                            intent.putExtra("PHONE", address.phone)
                            intent.putExtra("STREET", address.street)
                            intent.putExtra("LABEL", address.label)
                            intent.putExtra("CITY", address.city)
                            intent.putExtra("STATE", address.state)
                            intent.putExtra("PINCODE", address.pinCode)
                            context.startActivity(intent)
                            true
                        }

                        R.id.action_delete -> {
                            onDeleteClicked(address, position)
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun getItemCount(): Int = addressList.size

    inner class SavedAddressViewHolder(val binding: ItemSavedAddressesBinding):
            RecyclerView.ViewHolder(binding.root)

    fun removeAt(position: Int) {
        addressList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Helper to update address after edit
    fun updateAt(position: Int, updatedAddress: SavedAddress) {
        addressList[position] = updatedAddress
        notifyItemChanged(position)
    }
}