package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class AptInstall(private val packages: Array<out String>, private val options: String, private val reinstall: Boolean = false) : BashCmd {

    override fun render(sudo: Boolean): String =
        maybeSudo(sudo) + "DEBIAN_FRONTEND=noninteractive apt ai.chatinfra.context.xfce.install $options ${
            if (reinstall) "--reinstall" else ""
        } -o Dpkg::Options::=\"--force-confdef\" -o Dpkg::Options::=\"--force-confold\"  ${packages.joinToString(separator = " ") { "$it " }} -yq \n\n"
}
