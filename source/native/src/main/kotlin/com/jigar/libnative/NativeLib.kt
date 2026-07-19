package com.jigar.libnative

object NativeLib {
    external fun calculateSize(path: String): Long
    external fun getUidGid(path: String): IntArray
}