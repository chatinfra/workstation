package ai.chatinfra.bashscript.cmd

import ai.chatinfra.ai.chatinfra.bashscript.BashCmd
import ai.chatinfra.bashscript.Bash

class SetupAwsClient( val config: String,  val credentials: String) : BashCmd {
    override fun render(sudo: Boolean): String {
        return Bash {
            writeFile("/root/.aws/config", this@SetupAwsClient.config)
            writeFile("/root/.aws/credentials", this@SetupAwsClient.credentials)
        }.render()
    }
}
