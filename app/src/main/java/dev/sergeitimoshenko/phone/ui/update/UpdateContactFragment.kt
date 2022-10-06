package dev.sergeitimoshenko.phone.ui.update

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.R
import dev.sergeitimoshenko.phone.databinding.FragmentUpdateContactBinding
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.ui.phone.PhoneViewModel
import dev.sergeitimoshenko.phone.utils.compressBitmap
import dev.sergeitimoshenko.phone.utils.fromByteArray

private const val TAG = "UpdateContactFragment"

@AndroidEntryPoint
class UpdateContactFragment : Fragment(R.layout.fragment_update_contact) {
    private var binding: FragmentUpdateContactBinding? = null
    private val viewModel: PhoneViewModel by activityViewModels()
    private val args: UpdateContactFragmentArgs by navArgs()


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUpdateContactBinding.bind(view)

        val contact = args.contact

        var photo: Bitmap? = contact.photo
        val startImagePickerForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                photo = if (Build.VERSION.SDK_INT > 28) {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                photo = compressBitmap(photo)
                binding?.updateContact?.ivPhoto?.setImageBitmap(photo)
            }

        binding?.updateContact?.apply {
            etName.setText(contact.name)
            etSurname.setText(contact.surname)
            etPhone.setText(contact.phone)
            etEmail.setText(contact.email)
            cbIsFavourite.isChecked = contact.isFavourite

            if (contact.photo != null) {
                ivPhoto.setImageBitmap(contact.photo)
            }

            cvPhoto.setOnClickListener {
                startImagePickerForResult.launch("image/*")
            }

            btnSave.setOnClickListener {
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val phone = etPhone.text.toString()
                val email = etEmail.text.toString()
                val isFavourite = cbIsFavourite.isChecked
                val updatedContact = Contact(
                    name, surname, phone, email, photo, isFavourite, contact.id
                )

                if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty()) {
                    viewModel.updateContact(
                        updatedContact, contact.phone
                    )
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(), "Enter name, surname and phone number", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}