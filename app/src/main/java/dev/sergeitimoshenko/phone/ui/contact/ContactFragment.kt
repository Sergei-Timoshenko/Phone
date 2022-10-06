package dev.sergeitimoshenko.phone.ui.contact

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.R
import dev.sergeitimoshenko.phone.databinding.FragmentContactBinding
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.models.Photo
import dev.sergeitimoshenko.phone.ui.phone.PhoneViewModel
import dev.sergeitimoshenko.phone.utils.REQUEST_CALL
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_KEY
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_NAME
import dev.sergeitimoshenko.phone.utils.fromBitmap
import javax.inject.Inject

@AndroidEntryPoint
class ContactFragment : Fragment(R.layout.fragment_contact) {
    private var binding: FragmentContactBinding? = null
    private val viewModel: PhoneViewModel by activityViewModels()
    private val args: ContactFragmentArgs by navArgs()

    @Inject
    lateinit var signInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactBinding.bind(view)

        val sharedPreferences =
            activity?.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences?.edit()) {
            this?.putBoolean(SHARED_PREFERENCES_KEY, true)
            this?.apply()
        }

        var contact = args.contact
        val position = args.position
        val phoneNumber: String? = args.phoneNumber

        var keypadPosition = 0
        viewModel.contacts.value?.forEachIndexed { index, contact ->
            if (contact.phone == phoneNumber) {
                keypadPosition = index
            }
        }

        if (position == -1) {
            contact = viewModel.contacts.value?.get(keypadPosition)?.copy()!!
        }

        binding?.apply {
            if (contact.photo != null) {
                ivPhoto.setImageBitmap(contact.photo)
            }
            if (contact.email != null && contact.email != "") {
                tvEmail.visibility = View.VISIBLE
                tvEmail.text = contact.email
            }
            tvName.text = contact.name
            tvSurname.text = contact.surname
            tvPhone.text = contact.phone

            ibtnEdit.setOnClickListener {
                val photo = Photo(fromBitmap(contact.photo))
                val action = ContactFragmentDirections.actionContactFragmentToUpdateContactFragment(
                    contact.copy(photo = null), photo
                )
                findNavController().navigate(action)
            }

            ibtnCall.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.CALL_PHONE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL
                    )
                } else {
                    val callIntent = Intent(
                        Intent.ACTION_CALL, Uri.parse("tel:" + contact.phone)
                    )
                    startActivity(callIntent)
                }
            }

            ibtnSms.setOnClickListener {
                val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.phone))
                startActivity(smsIntent)
            }

            ibtnEmail.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + contact.email))
                startActivity(emailIntent)
            }
        }

        when (contact.isFavourite) {
            false -> {
                viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
                    updateContactInfo(contacts, position)
                }
            }
            true -> {
                viewModel.favouriteContacts.observe(viewLifecycleOwner) { favouriteContacts ->
                    updateContactInfo(favouriteContacts, position)
                }
            }
        }
    }

    private fun updateContactInfo(contacts: List<Contact>, position: Int) {
        if (position != RecyclerView.NO_POSITION && contacts.isNotEmpty()) {
            binding?.apply {
                if (contacts[position].photo != null) {
                    ivPhoto.setImageBitmap(contacts[position].photo)
                }
                if (contacts[position].email != null && contacts[position].email != "") {
                    tvEmail.visibility = View.VISIBLE
                    tvEmail.text = contacts[position].email
                } else {
                    tvEmail.visibility = View.GONE
                }
                tvName.text = contacts[position].name
                tvSurname.text = contacts[position].surname
                tvPhone.text = contacts[position].phone
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}