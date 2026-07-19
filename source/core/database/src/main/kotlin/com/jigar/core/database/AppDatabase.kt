package com.jigar.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jigar.core.database.dao.CloudDao
import com.jigar.core.database.dao.DirectoryDao
import com.jigar.core.database.dao.LabelDao
import com.jigar.core.database.dao.MediaDao
import com.jigar.core.database.dao.PackageDao
import com.jigar.core.database.dao.TaskDao
import com.jigar.core.database.util.StringListConverters
import com.jigar.core.model.database.CloudEntity
import com.jigar.core.model.database.DirectoryEntity
import com.jigar.core.model.database.LabelAppCrossRefEntity
import com.jigar.core.model.database.LabelEntity
import com.jigar.core.model.database.LabelFileCrossRefEntity
import com.jigar.core.model.database.MediaEntity
import com.jigar.core.model.database.PackageEntity
import com.jigar.core.model.database.ProcessingInfoEntity
import com.jigar.core.model.database.TaskDetailMediaEntity
import com.jigar.core.model.database.TaskDetailPackageEntity
import com.jigar.core.model.database.TaskEntity

@Database(
    version = 7,
    exportSchema = true,
    entities = [
        PackageEntity::class,
        MediaEntity::class,
        DirectoryEntity::class,
        CloudEntity::class,
        TaskEntity::class,
        TaskDetailPackageEntity::class,
        TaskDetailMediaEntity::class,
        ProcessingInfoEntity::class,
        LabelEntity::class,
        LabelAppCrossRefEntity::class,
        LabelFileCrossRefEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
        AutoMigration(from = 3, to = 4, spec = DatabaseMigrations.Schema3to4::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6, spec = DatabaseMigrations.Schema5to6::class),
        AutoMigration(from = 6, to = 7),
    ]
)
@TypeConverters(StringListConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao
    abstract fun mediaDao(): MediaDao
    abstract fun taskDao(): TaskDao
    abstract fun directoryDao(): DirectoryDao
    abstract fun cloudDao(): CloudDao
    abstract fun labelDao(): LabelDao
}
