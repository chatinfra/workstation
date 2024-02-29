package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class Echo(private val message: String) : BashCmd {
    override fun render(sudo: Boolean): String = "echo $message\n\n"
}
