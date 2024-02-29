package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 val mkdirSkip: List<String> = listOf("/root", "/etc", "/var", "/usr")

 class MkDir(private val dir: String) : BashCmd {
    override fun render(sudo: Boolean): String {
        return "mkdir -p $dir\n\n"
    }
}
