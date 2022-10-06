package dev.sergeitimoshenko.phone.ui.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.R
import dev.sergeitimoshenko.phone.databinding.ActivitySignInBinding
import dev.sergeitimoshenko.phone.ui.phone.PhoneActivity
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_KEY
import dev.sergeitimoshenko.phone.utils.SHARED_PREFERENCES_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "SignInActivity"

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val viewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var signInIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(SHARED_PREFERENCES_KEY, true)
            apply()
        }

        val startGoogleActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { intent ->
                val signInAccount = GoogleSignIn.getSignedInAccountFromIntent(intent.data).result
                signInAccount?.let { account ->
                    googleAuthForFirebase(account)
                }
            }

        binding.btnSignIn.setOnClickListener {
            startGoogleActivityForResult.launch(signInIntent)
        }
    }

    private fun googleAuthForFirebase(account: GoogleSignInAccount) {
        val sharedPreferences = getSharedPreferences(
            SHARED_PREFERENCES_NAME, MODE_PRIVATE
        )
        val isFirstLogin = sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY, false)
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credentials).await()
                runOnUiThread {
                    if (isFirstLogin) {
                      viewModel.loadContacts()
                        with(sharedPreferences.edit()) {
                            putBoolean(SHARED_PREFERENCES_KEY, false)
                            apply()
                        }
                    }
                }
                navigateToPhoneActivity()
            } catch (e: Exception) {
                Log.d(TAG, "googleAuthForFirebase: ${e.message}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToPhoneActivity()
        }
    }

    private fun navigateToPhoneActivity() {
        val intent = Intent(this@SignInActivity, PhoneActivity::class.java)
        startActivity(intent)
    }
}

