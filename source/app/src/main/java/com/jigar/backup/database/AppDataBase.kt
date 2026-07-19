package com.jigar.backup.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jigar.backup.database.dao.AppDao
import com.jigar.backup.database.dao.CallLogDao
import com.jigar.backup.database.dao.ContactDao
import com.jigar.backup.database.dao.MessageDao
import com.jigar.backup.database.dao.NetworkDao
import com.jigar.backup.database.entity.App
import com.jigar.backup.database.entity.CallLog
import com.jigar.backup.database.entity.Contact
import com.jigar.backup.database.entity.Mms
import com.jigar.backup.database.entity.Network
import com.jigar.backup.database.entity.Sms

@Database(
    entities = [
        App::class,
        Network::class,
        Contact::class,
        CallLog::class,
        Sms::class,
        Mms::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun networkDao(): NetworkDao
    abstract fun contactDao(): ContactDao
    abstract fun callLogDao(): CallLogDao
    abstract fun messageDao(): MessageDao
}
