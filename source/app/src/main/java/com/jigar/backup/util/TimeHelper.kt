package com.jigar.backup.util

import com.jigar.backup.App
import com.jigar.backup.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeHelper {
    fun formatTimestampInShort(timestamp: Long): String {
        return runCatching {
            SimpleDateFormat(
                App.application.getString(R.string.time_format_pattern),
                Locale.getDefault()
            ).format(
                Date(timestamp)
            )
        }.getOrNull() ?: App.application.getString(R.string.unknown)
    }

    fun formatTimestampInDetail(timestamp: Long): String {
        return runCatching {
            SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss",
                Locale.getDefault()
            ).format(
                Date(timestamp)
            )
        }.getOrNull() ?: App.application.getString(R.string.unknown)
    }
}
