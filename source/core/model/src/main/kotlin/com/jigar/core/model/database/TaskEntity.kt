package com.jigar.core.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jigar.core.model.OpType
import com.jigar.core.model.TaskType

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var opType: OpType,
    var taskType: TaskType,
    var startTimestamp: Long,
    var endTimestamp: Long,
    var rawBytes: Double = 0.toDouble(),
    var availableBytes: Double = 0.toDouble(),
    var totalBytes: Double = 0.toDouble(),
    var totalCount: Int = 0,
    var successCount: Int = 0,
    var failureCount: Int = 0,
    @ColumnInfo(defaultValue = "0") var preprocessingIndex: Int = 0,
    var processingIndex: Int = 0,
    @ColumnInfo(defaultValue = "0") var postProcessingIndex: Int = 0,
    var isProcessing: Boolean,
    var cloud: String = "",
    var backupDir: String,
)
