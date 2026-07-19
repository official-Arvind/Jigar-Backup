package com.jigar.backup.data

import com.jigar.backup.App.Companion.application
import com.jigar.backup.database.entity.Mms
import com.jigar.backup.database.entity.Sms
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.MessagesOptionSelectedBackup
import com.jigar.backup.util.readBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageRepository {
    companion object {
        private const val TAG = "MessageRepository"
    }

    val isBackupContactsSelected: Flow<Boolean> = application.readBoolean(MessagesOptionSelectedBackup)

    val smsList: Flow<List<Sms>> = DatabaseHelper.messageDao.loadFlowSms()
    val smsListSelected: Flow<List<Sms>> = smsList.map { smsList -> smsList.filter { it.selected } }

    val mmsList: Flow<List<Mms>> = DatabaseHelper.messageDao.loadFlowMms()
    val mmsListSelected: Flow<List<Mms>> = mmsList.map { mmsList -> mmsList.filter { it.selected } }
}
