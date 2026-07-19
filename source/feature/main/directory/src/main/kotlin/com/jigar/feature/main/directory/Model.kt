package com.jigar.feature.main.directory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.SdCard
import com.jigar.core.model.StorageType
import com.jigar.core.model.database.DirectoryEntity

fun DirectoryEntity.icon() = when (storageType) {
    StorageType.INTERNAL -> Icons.Rounded.PhoneAndroid
    StorageType.EXTERNAL -> Icons.Rounded.SdCard
    StorageType.CUSTOM -> Icons.Rounded.Palette
}

fun DirectoryEntity.pathDisplay() = when (storageType) {
    StorageType.EXTERNAL -> if (type.isEmpty()) path else "$path ($type)"
    else -> path
}
