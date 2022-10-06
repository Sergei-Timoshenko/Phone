package dev.sergeitimoshenko.phone.repositories

import dev.sergeitimoshenko.phone.models.Contact
import javax.inject.Inject

class MainRepository @Inject constructor(
    val firebaseRepository: FirebaseRepository,
    val roomRepository: RoomRepository,
) {
    suspend fun insertContact(contact: Contact) {
        roomRepository.insertContact(contact)
    }

    suspend fun insertFirebase(contact: Contact) {
        firebaseRepository.insertContactIntoFirestore(contact)
    }

    suspend fun deleteContact(contact: Contact) {
        roomRepository.deleteContact(contact)
        firebaseRepository.deleteContactFromFirestore(contact)
    }

    suspend fun updateContact(contact: Contact, oldPhone: String) {
        roomRepository.updateContact(contact)
        firebaseRepository.updateContactInFirestore(contact, oldPhone)
    }

    suspend fun deleteAllContacts(contacts: List<Contact>) {
        contacts.forEach { contact ->
            roomRepository.deleteContact(contact)
        }
    }
}