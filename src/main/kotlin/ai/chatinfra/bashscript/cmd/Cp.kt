package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 data class Cp(val source: String, val destination: String) : BashCmd {
    override fun render(sudo: Boolean): String = maybeSudo(sudo) + "cp $source $destination"
}
