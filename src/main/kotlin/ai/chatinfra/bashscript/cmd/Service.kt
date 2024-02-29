package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import ai.chatinfra.bashscript.Bash

data class Service( val serviceName: String,  val action: String, val ignoreError: Boolean = false) : BashCmd {
    override fun render(sudo: Boolean): String {
        return "systemctl $action $serviceName ${if(ignoreError) "|| true " else ""} \n" + if (action == Bash.Action.Enable.cmd) {
            "systemctl daemon-reload"
        } else {
            ""
        }
    }
}
