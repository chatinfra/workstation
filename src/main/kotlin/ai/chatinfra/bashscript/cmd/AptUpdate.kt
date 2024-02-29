package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class AptUpdate() : BashCmd {
    override fun render(sudo: Boolean): String = "DEBIAN_FRONTEND=noninteractive apt update -yq \n"
}
