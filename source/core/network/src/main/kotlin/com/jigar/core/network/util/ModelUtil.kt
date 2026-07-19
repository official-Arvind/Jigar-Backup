package com.jigar.core.network.util

import com.google.gson.reflect.TypeToken
import com.jigar.core.model.database.CloudEntity
import com.jigar.core.util.GsonUtil

inline fun <reified T> CloudEntity.getExtraEntity() = runCatching { GsonUtil().fromJson<T>(extra, object : TypeToken<T>() {}.type) }.getOrNull()
