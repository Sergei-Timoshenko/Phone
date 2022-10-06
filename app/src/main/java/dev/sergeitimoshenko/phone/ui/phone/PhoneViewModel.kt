package dev.sergeitimoshenko.phone.ui.phone

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val favouriteContacts = Transformations.switchMap(searchQuery) {
        mainRepository.roomRepository.contactDao.getFavouriteContacts(searchQuery.value!!)
    }

    val contacts = Transformations.switchMap(searchQuery) {
        mainRepository.roomRepository.contactDao.getContacts(searchQuery.value!!)
    }

    fun setSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery
    }

    fun insertContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertContact(contact)
    }

    fun insertFirebase(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.insertFirebase(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteContact(contact)
    }

    fun updateContact(contact: Contact, oldPhone: String) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.updateContact(contact, oldPhone)
    }

    fun deleteAllContacts() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteAllContacts(contacts.value!!)
    }

    fun loadContacts() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.firebaseRepository.firestore.collection(auth.currentUser?.uid!!)
            .get().addOnSuccessListener { snapshots ->
                for (snapshot in snapshots) {
                    val currentContact =
                        mainRepository.firebaseRepository.contactFirestoreMapper.mapFromEntity(
                            snapshot.data
                        )
                    insertContact(currentContact)
                }
            }
    }
}