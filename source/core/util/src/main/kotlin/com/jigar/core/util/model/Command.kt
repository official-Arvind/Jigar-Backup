package com.jigar.core.util.model

import com.jigar.core.common.util.toLineString
import com.jigar.core.common.util.toSpaceString

data class ShellResult(
    var code: Int,
    var input: List<String>,
    var out: List<String>,
) {
    val isSuccess: Boolean
        get() = code == 0

    val inputString: String
        get() = input.toSpaceString()

    val outString: String
        get() = out.toLineString()
}
