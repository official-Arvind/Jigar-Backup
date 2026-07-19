package com.jigar.backup.data

import com.jigar.backup.App.Companion.application
import com.jigar.backup.database.entity.Contact
import com.jigar.backup.util.ContactsOptionSelectedBackup
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.readBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactRepository {
    companion object {
        private const val TAG = "ContactRepository"
    }

    val isBackupMessagesSelected: Flow<Boolean> = application.readBoolean(ContactsOptionSelectedBackup)

    val contacts: Flow<List<Contact>> = DatabaseHelper.contactDao.loadFlowContacts()
    val contactsSelected: Flow<List<Contact>> = contacts.map { contacts -> contacts.filter { it.selected } }
}
