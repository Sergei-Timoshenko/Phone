package dev.sergeitimoshenko.phone.ui.add

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
import dev.sergeitimoshenko.phone.databinding.FragmentAddContactBinding
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.ui.phone.PhoneViewModel
import dev.sergeitimoshenko.phone.utils.compressBitmap
import dev.sergeitimoshenko.phone.utils.fromBitmap
import dev.sergeitimoshenko.phone.utils.fromByteArray

@AndroidEntryPoint
class AddContactFragment : Fragment(R.layout.fragment_add_contact) {
    private var binding: FragmentAddContactBinding? = null
    private val viewModel: PhoneViewModel by activityViewModels()
    private val args: AddContactFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddContactBinding.bind(view)

        if (!args.phoneNumber.isNullOrEmpty()) {
            binding?.addContact?.etPhone?.setText(args.phoneNumber)
        }

        var id: Int

        if (args.isFavourite) {
            binding?.addContact?.cbIsFavourite?.isChecked = true
        }

        var photo: Bitmap? = null
        val startImagePickerForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                photo = if (Build.VERSION.SDK_INT > 28) {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                photo = compressBitmap(photo)
                binding?.addContact?.ivPhoto?.setImageBitmap(photo)
            }


        binding?.addContact?.apply {
            cvPhoto.setOnClickListener {
                startImagePickerForResult.launch("image/*")
            }

            btnSave.setOnClickListener {
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val phone = etPhone.text.toString()
                val email = etEmail.text.toString()
                val isFavourite = cbIsFavourite.isChecked
                val contact = Contact(
                    name, surname, phone, email, photo, isFavourite
                )

                if (name.isNotEmpty() && surname.isNotEmpty() && phone.isNotEmpty()) {
                    viewModel.insertContact(contact)
                } else {
                    Toast.makeText(
                        requireContext(), "Enter name, surname and phone number", Toast.LENGTH_SHORT
                    ).show()
                }

                viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
                    contacts.forEachIndexed { index, it ->
                        if (contact.phone == it.phone) {
                            id = index
                            viewModel.insertFirebase(contact.copy(id = contact.id))
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}