package com.jigar.core.util.command

import com.jigar.core.common.util.toSpaceString
import com.jigar.core.common.util.trim
import com.jigar.core.util.SymbolUtil.QUOTE
import com.jigar.core.util.model.ShellResult

object Tree {
    private suspend fun execute(vararg args: String): ShellResult = BaseUtil.execute("tree", *args)
    suspend fun tree(src: String, exclusionList: List<String>): ShellResult = run {
        val exclusion = exclusionList.trim().map { "-I $it" }.toSpaceString()
        // tree -N "$src" '$exclusion'
        execute(
            "-N",
            "$QUOTE$src$QUOTE",
            exclusion,
        )
    }
}
