package dev.sergeitimoshenko.phone.ui.keypad

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.R
import dev.sergeitimoshenko.phone.databinding.FragmentKeypadBinding
import dev.sergeitimoshenko.phone.ui.signin.SignInActivity
import dev.sergeitimoshenko.phone.utils.REQUEST_CALL
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KeypadFragment : Fragment(R.layout.fragment_keypad), MenuProvider {
    private lateinit var binding: FragmentKeypadBinding
    private val viewModel: KeypadViewModel by activityViewModels()
    private var sharedPreferences: SharedPreferences? = null

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var signInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKeypadBinding.bind(view)

        sharedPreferences =
            activity?.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        binding.apply {
            cvOne.setOnClickListener {
                viewModel.enterDigit("1")
            }

            cvTwo.setOnClickListener {
                viewModel.enterDigit("2")
            }

            cvThree.setOnClickListener {
                viewModel.enterDigit("3")
            }

            cvFour.setOnClickListener {
                viewModel.enterDigit("4")
            }

            cvFive.setOnClickListener {
                viewModel.enterDigit("5")
            }

            cvSix.setOnClickListener {
                viewModel.enterDigit("6")
            }

            cvSeven.setOnClickListener {
                viewModel.enterDigit("7")
            }

            cvEight.setOnClickListener {
                viewModel.enterDigit("8")
            }

            cvNine.setOnClickListener {
                viewModel.enterDigit("9")
            }

            cvZero.setOnClickListener {
                viewModel.enterDigit("0")
            }

            cvRemove.setOnClickListener {
                viewModel.removeDigit()
            }

            cvRemove.setOnLongClickListener {
                viewModel.removeAllDigits()
                true
            }

            cvCall.setOnClickListener {
                if (viewModel.phoneNumber.value != "") {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL
                        )
                    } else {
                        val callIntent = Intent(
                            Intent.ACTION_CALL, Uri.parse("tel:" + viewModel.phoneNumber.value)
                        )
                        startActivity(callIntent)
                    }
                }
            }

            ibtnAddContact.setOnClickListener {
                val action =
                    KeypadFragmentDirections.actionKeypadFragmentToAddContactFragment(viewModel.phoneNumber.value!!)
                findNavController().navigate(action)
            }

            tvContactName.setOnClickListener {
                val action = KeypadFragmentDirections.actionKeypadFragmentToContactFragment(
                    viewModel.contact.value!!, phoneNumber = viewModel.phoneNumber.value
                )
                findNavController().navigate(action)
            }
        }

        viewModel.contact.observe(viewLifecycleOwner) { contact ->
            if (contact != null) {
                binding.apply {
                    tvContactName.apply {
                        text = "${contact.name} ${contact.surname}"
                        visibility = View.VISIBLE
                    }
                    ibtnAddContact.visibility = View.GONE
                }
            } else {
                binding.apply {
                    if (viewModel.phoneNumber.value != "") {
                        ibtnAddContact.visibility = View.VISIBLE
                    }
                    tvContactName.visibility = View.GONE
                }
            }
        }

        viewModel.phoneNumber.observe(viewLifecycleOwner) { phoneNumber ->
            binding.tvPhoneNumber.text = phoneNumber

            if (phoneNumber != "") {
                binding.apply {
                    if (binding.tvContactName.visibility != View.VISIBLE) {
                        ibtnAddContact.visibility = View.VISIBLE
                    }
                    cvRemove.visibility = View.VISIBLE
                    cvCall.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    ibtnAddContact.visibility = View.GONE
                    cvRemove.visibility = View.GONE
                    cvCall.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.keypad_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_open_settings) {
            lifecycleScope.launch(Dispatchers.IO) {
                with(sharedPreferences?.edit()) {
                    this?.putBoolean(
                        dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_KEY, true
                    )
                    this?.apply()
                }
            }.invokeOnCompletion {
                auth.signOut()
                signInClient.signOut()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}