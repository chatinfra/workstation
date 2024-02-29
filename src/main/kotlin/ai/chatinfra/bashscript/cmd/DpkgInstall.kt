package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 data class DpkgInstall(val filename: String) : BashCmd {
    override fun render(sudo: Boolean): String = maybeSudo(sudo) + "DEBIAN_FRONTEND=noninteractive dpkg -i $filename"
}
