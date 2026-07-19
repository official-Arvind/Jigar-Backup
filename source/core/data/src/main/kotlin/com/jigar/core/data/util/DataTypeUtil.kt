package com.jigar.core.data.util

import com.jigar.core.model.DataType
import com.jigar.core.util.PathUtil

fun DataType.srcDir(userId: Int): String = when (this) {
    DataType.PACKAGE_USER -> PathUtil.getPackageUserDir(userId)
    DataType.PACKAGE_USER_DE -> PathUtil.getPackageUserDeDir(userId)
    DataType.PACKAGE_DATA -> PathUtil.getPackageDataDir(userId)
    DataType.PACKAGE_OBB -> PathUtil.getPackageObbDir(userId)
    DataType.PACKAGE_MEDIA -> PathUtil.getPackageMediaDir(userId)
    else -> ""
}
