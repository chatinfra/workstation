package ai.chatinfra.context.systemcall

import ai.chatinfra.ai.chatinfra.context.MinimalContext
import ai.chatinfra.ai.chatinfra.server.Localhost
import ai.chatinfra.ai.chatinfra.context.systemcall.SystemCallResult
import ai.chatinfra.ai.chatinfra.util.Res
import ai.chatinfra.bashscript.Bash

interface SystemCallContext : MinimalContext {
    val mc: MinimalContext
    val localhost: Localhost
    val execBash: suspend (script: Bash) -> Res<SystemCallResult>
    val execCmd: suspend (cmd: String) -> Res<SystemCallResult>
}


data class SystemCallContextImpl(
    override val mc: MinimalContext,
    override val localhost: Localhost,
    override val execBash: suspend (script: Bash) -> Res<SystemCallResult>,
    override val execCmd: suspend (cmd: String) -> Res<SystemCallResult>
) : SystemCallContext, MinimalContext by mc {


}

