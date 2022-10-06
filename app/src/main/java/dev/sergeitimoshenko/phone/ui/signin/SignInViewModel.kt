package dev.sergeitimoshenko.phone.ui.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergeitimoshenko.phone.db.ContactDao
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.remote.firestore.wrappers.ContactFirestoreMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SignInViewModel"

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val contactDao: ContactDao,
    private val firestore: FirebaseFirestore,
    private val contactFirestoreMapper: ContactFirestoreMapper,
    private val auth: FirebaseAuth
) : ViewModel() {
    private fun insertContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        contactDao.insertContact(contact)
    }

    fun loadContacts() = viewModelScope.launch(Dispatchers.IO) {
        val firestoreRef =
            firestore.collection("users").document(auth.currentUser?.uid!!).collection("phones")
        val firestoreSnapshot = firestoreRef.get().addOnSuccessListener{ snapshots ->
            if (snapshots.size() > 0) {
                for (snapshot in snapshots) {
                    val currentContact =
                        contactFirestoreMapper.mapFromEntity(
                            snapshot.data
                        )
                    Log.d(TAG, "loadContacts: $currentContact")
                    insertContact(currentContact)
                }
            }
        }
    }
}