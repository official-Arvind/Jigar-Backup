package com.jigar.core.hiddenapi

inline fun <reified T> Any.castTo(): T {
    return this as T
}
