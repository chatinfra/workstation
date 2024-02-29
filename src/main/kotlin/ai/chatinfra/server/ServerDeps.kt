package ai.chatinfra.ai.chatinfra.server

import ai.chatinfra.ai.chatinfra.context.MinimalContext
import ai.chatinfra.ai.chatinfra.context.systemcall.SystemCallResult
import ai.chatinfra.ai.chatinfra.check.CheckDeps
import ai.chatinfra.ai.chatinfra.util.Res
import ai.chatinfra.bashscript.Bash


interface ServerDeps : CheckDeps {
    val mc: MinimalContext
    val server: Server
    val execScript: suspend (script: Bash) -> Res<SystemCallResult>
}

data class ServerDepsImpl(
    override val mc: MinimalContext,
    override val server: Server,
    override val execScript: suspend (script: Bash) -> Res<SystemCallResult>
) : ServerDeps {


}