package ai.chatinfra.ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class AptAutoRemove() : BashCmd {
    override fun render(sudo: Boolean): String = "DEBIAN_FRONTEND=noninteractive apt autoremove -yq\n"
}
