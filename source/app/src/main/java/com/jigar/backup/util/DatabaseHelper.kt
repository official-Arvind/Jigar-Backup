package com.jigar.backup.util

import androidx.room.Room
import com.jigar.backup.App
import com.jigar.backup.database.AppDatabase

object DatabaseHelper {
    private val database = Room.databaseBuilder(
        App.application,
        AppDatabase::class.java,
        "database-jigarbackup"
    ).build()

    val appDao = database.appDao()
    val networkDao = database.networkDao()
    val contactDao = database.contactDao()
    val callLogDao = database.callLogDao()
    val messageDao = database.messageDao()
}
