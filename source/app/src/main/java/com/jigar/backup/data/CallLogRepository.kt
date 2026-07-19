package com.jigar.backup.data

import com.jigar.backup.App.Companion.application
import com.jigar.backup.database.entity.CallLog
import com.jigar.backup.util.CallLogsOptionSelectedBackup
import com.jigar.backup.util.DatabaseHelper
import com.jigar.backup.util.readBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallLogRepository {
    companion object {
        private const val TAG = "CallLogRepository"
    }

    val isBackupCallLogsSelected: Flow<Boolean> = application.readBoolean(CallLogsOptionSelectedBackup)

    val callLogs: Flow<List<CallLog>> = DatabaseHelper.callLogDao.loadFlowCallLogs()
    val callLogsSelected: Flow<List<CallLog>> = callLogs.map { callLogs -> callLogs.filter { it.selected } }
}
