package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd

 class AptDistUpgrade( val distUpgrade: Boolean = false,  val autoremove: Boolean = true) : BashCmd {
    override fun render(sudo: Boolean): String = """
DEBIAN_FRONTEND=noninteractive apt update -yq 
DEBIAN_FRONTEND=noninteractive apt dist-upgrade -o Dpkg::Options::="--force-confdef" -o Dpkg::Options::="--force-confold" -yq
        
    """.trimIndent()
}
