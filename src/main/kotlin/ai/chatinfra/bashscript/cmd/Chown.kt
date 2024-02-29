package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class Chown(private val dir: String,  val userGroup: String,  val recursive: Boolean) : BashCmd {
    override fun render(sudo: Boolean): String {
        val builder = StringBuilder()
        builder.append("chown ")
        if (recursive) {
            builder.append("-R ")
        }
        builder.append(userGroup).append(" ${dir}\n")
        return builder.toString()
    }
}
