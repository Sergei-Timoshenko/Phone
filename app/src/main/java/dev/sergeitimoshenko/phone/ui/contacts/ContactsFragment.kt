package dev.sergeitimoshenko.phone.ui.contacts

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.R
import dev.sergeitimoshenko.phone.adapters.ContactsAdapter
import dev.sergeitimoshenko.phone.databinding.FragmentContactsBinding
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.models.Photo
import dev.sergeitimoshenko.phone.ui.phone.PhoneViewModel
import dev.sergeitimoshenko.phone.ui.signin.SignInActivity
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_KEY
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_NAME
import dev.sergeitimoshenko.phone.utils.fromBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

private const val TAG = "ContactsFragment"

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts),
    ContactsAdapter.OnContactClickListener, MenuProvider {
    private lateinit var binding: FragmentContactsBinding
    private val viewModel: PhoneViewModel by activityViewModels()
    private lateinit var contactsAdapter: ContactsAdapter
    private var sharedPreferences: SharedPreferences? = null

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var signInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContactsBinding.bind(view)

        sharedPreferences =
            activity?.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        contactsAdapter = ContactsAdapter(this)
        binding.rvContacts.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.START or ItemTouchHelper.END
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val contact = viewModel.contacts.value!![position]
                    viewModel.deleteContact(contact)
                }
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.rvContacts)

        binding.fabAddContact.setOnClickListener {
            val action = ContactsFragmentDirections.actionContactsFragmentToAddContactFragment(null)
            findNavController().navigate(action)
        }

        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactsAdapter.submitList(contacts)
        }

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    override fun onContactClick(contact: Contact, position: Int) {
        val action =
            ContactsFragmentDirections.actionContactsFragmentToContactFragment(contact, position)
        findNavController().navigate(action)
    }

    override fun onEditButtonClick(contact: Contact) {
        val photo = Photo(fromBitmap(contact.photo))
        val action = ContactsFragmentDirections.actionContactsFragmentToUpdateContactFragment(
            contact.copy()
        )
        findNavController().navigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.contacts_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_search -> {
                val searchView = menuItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = true

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText!!)
                        return true
                    }
                })
            }
            R.id.action_open_contacts_settings -> {
                viewModel.deleteAllContacts().invokeOnCompletion {
                    with(sharedPreferences?.edit()) {
                        this?.putBoolean(SHARED_PREFERENCES_KEY, true)
                        this?.apply()
                    }
                    auth.signOut()
                    signInClient.signOut()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return false
    }
}