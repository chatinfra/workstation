package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class ExecuteCommand(private val frag: String) : BashCmd {
    override fun render(sudo: Boolean): String {
        return "$frag\n\n"
    }
}
