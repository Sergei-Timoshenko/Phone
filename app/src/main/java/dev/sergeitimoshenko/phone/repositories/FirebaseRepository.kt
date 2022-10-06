package dev.sergeitimoshenko.phone.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.sergeitimoshenko.phone.models.Contact
import dev.sergeitimoshenko.phone.remote.firestore.wrappers.ContactFirestoreMapper
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    val firestore: FirebaseFirestore,
    val auth: FirebaseAuth,
    val contactFirestoreMapper: ContactFirestoreMapper
) {
    fun insertContactIntoFirestore(contact: Contact) {
        firestore.collection("users").document(auth.currentUser?.uid!!).collection(contact.id).document(contact.phone).set(
                contactFirestoreMapper.mapToEntity(
                    contact
                )
            )
    }

    fun deleteContactFromFirestore(contact: Contact) {
        firestore.collection("users").document(auth.currentUser?.uid!!).collection(contact.id).document(contactFirestoreMapper.mapToEntity(contact).phone)
            .delete()
    }

    fun updateContactInFirestore(contact: Contact, oldPhone: String) {
        firestore.collection("users").document(auth.currentUser?.uid!!).collection(contact.id).document(oldPhone).delete()
        firestore.collection("users").document(auth.currentUser?.uid!!).collection(contact.id).document(contact.phone).set(
            contactFirestoreMapper.mapToEntity(
                contact
            )
        )
    }
}