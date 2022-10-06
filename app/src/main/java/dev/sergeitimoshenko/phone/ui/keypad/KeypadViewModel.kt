package dev.sergeitimoshenko.phone.ui.keypad

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sergeitimoshenko.phone.db.ContactDao
import javax.inject.Inject

@HiltViewModel()
class KeypadViewModel @Inject constructor(
    private val contactDao: ContactDao
): ViewModel() {
    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> = _phoneNumber

    val contact = Transformations.switchMap(_phoneNumber) {
        contactDao.getContactByPhone(_phoneNumber.value!!)
    }

    fun removeDigit() {
        _phoneNumber.value = _phoneNumber.value?.dropLast(1)
    }

    fun removeAllDigits() {
        _phoneNumber.value = ""
    }

    fun enterDigit(digit: String) {
        _phoneNumber.value = _phoneNumber.value + digit
    }


}