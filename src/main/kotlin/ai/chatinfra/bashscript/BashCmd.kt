package ai.chatinfra.ai.chatinfra.bashscript

import ai.chatinfra.bashscript.BashScriptTagMarker


@BashScriptTagMarker
interface BashCmd {
    fun render(sudo: Boolean = false): String

    fun maybeSudo(sudo: Boolean = true): String = if (sudo) {
        "sudo "
    } else {
        ""
    }
}
