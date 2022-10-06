package dev.sergeitimoshenko.phone.ui.phone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.sergeitimoshenko.phone.databinding.ActivityPhoneBinding
import dev.sergeitimoshenko.phone.utils.REQUEST_CALL
import javax.inject.Inject

@AndroidEntryPoint
class PhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appTitle = "${FirebaseAuth.getInstance().currentUser?.displayName?.substringBefore(" ")}'s contacts"
        title = appTitle

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fcvMain.id)
        val navController = navHostFragment!!.findNavController()
        binding.bnvMain.setupWithNavController(navController)

        if (ContextCompat.checkSelfPermission(
                binding.root.context, Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL
            )
        }
    }
}
