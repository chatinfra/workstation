package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class UfwAllow(private val ports: List<Int>) : BashCmd {
    override fun render(sudo: Boolean): String = ports.map { "ufw allow $it" }.joinToString("\n") + "\n"
}
