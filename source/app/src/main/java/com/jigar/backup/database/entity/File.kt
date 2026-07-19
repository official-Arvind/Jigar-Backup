package com.jigar.backup.database.entity

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "files", primaryKeys = ["path"])
data class File(
    var name: String,
    var path: String,
    var selected: Boolean,
)
