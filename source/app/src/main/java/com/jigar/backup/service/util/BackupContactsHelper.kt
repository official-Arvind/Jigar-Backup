package com.jigar.backup.service.util

import arrow.optics.copy
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.jigar.backup.App.Companion.application
import com.jigar.backup.R
import com.jigar.backup.data.BackupProcessRepository
import com.jigar.backup.data.ProcessItem
import com.jigar.backup.data.currentIndex
import com.jigar.backup.data.msg
import com.jigar.backup.data.progress
import com.jigar.backup.database.entity.Contact
import com.jigar.backup.rootservice.RemoteRootService
import com.jigar.backup.util.LogHelper
import com.jigar.backup.util.PathHelper

class BackupContactsHelper(private val mBackupProcessRepo: BackupProcessRepository) {
    companion object {
        private const val TAG = "BackupContactsHelper"
    }

    suspend fun start() {
        val contacts = mBackupProcessRepo.getContacts()
        contacts.forEachIndexed { index, contact ->
            mBackupProcessRepo.updateContactsItem {
                copy {
                    ProcessItem.currentIndex set index
                    ProcessItem.msg set contact.id.toString()
                    ProcessItem.progress set index.toFloat() / contacts.size
                }
            }
        }
        val json = runCatching {
            val moshi: Moshi = Moshi.Builder().build()
            moshi.adapter<List<Contact>>().toJson(contacts)
        }.onFailure {
            LogHelper.e(TAG, "start", "Failed to serialize to json.", it)
        }.getOrNull()
        if (json != null) {
            val backupConfig = mBackupProcessRepo.getBackupConfig()
            val contactsPath = PathHelper.getBackupContactsDir(backupConfig.path)
            if (RemoteRootService.exists(contactsPath).not() && RemoteRootService.mkdirs(contactsPath).not()) {
                LogHelper.e(TAG, "start", "Failed to mkdirs: $contactsPath.")
            }
            val configPath = PathHelper.getBackupContactsConfigFilePath(backupConfig.path)
            RemoteRootService.deleteRecursively(configPath)
            RemoteRootService.writeText(configPath, json)
        } else {
            LogHelper.e(TAG, "start", "Failed to save contacts, json is null")
        }

        mBackupProcessRepo.updateContactsItem {
            copy {
                ProcessItem.currentIndex set contacts.size
                ProcessItem.msg set application.getString(R.string.finished)
                ProcessItem.progress set 1f
            }
        }
    }
}
