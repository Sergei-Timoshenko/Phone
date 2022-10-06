package dev.sergeitimoshenko.phone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sergeitimoshenko.phone.databinding.ItemContactBinding
import dev.sergeitimoshenko.phone.models.Contact

class ContactsAdapter(
    private val listener: OnContactClickListener
) : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(
                oldItem: Contact, newItem: Contact
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Contact, newItem: Contact
            ) = oldItem == newItem
        }
    }

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnLongClickListener {
                    val currentContact = getItem(adapterPosition)
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onContactClick(currentContact, adapterPosition)
                    }
                    true
                }

                ibtnEdit.setOnClickListener {
                    val currentContact = getItem(adapterPosition)
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onEditButtonClick(currentContact)
                    }
                }
            }
        }

        fun bind(contact: Contact) {
            binding.apply {
                tvName.text = contact.name
                tvSurname.text = contact.surname
                tvPhone.text = contact.phone
                if (contact.photo != null) {
                    ivPhoto.setImageBitmap(contact.photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = getItem(position)
        holder.bind(currentContact)
    }

    interface OnContactClickListener {
        fun onContactClick(contact: Contact, position: Int)

        fun onEditButtonClick(contact: Contact)
    }
}