package ai.chatinfra.ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class AddLine(private val line: String, private val filename: String) : BashCmd {
    override fun render(sudo: Boolean): String {
        return "grep -qF -- \"$line\" $filename || echo \"$line\" >> $filename\n"
    }
}
