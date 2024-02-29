package ai.chatinfra.ai.chatinfra.check

import ai.chatinfra.ai.chatinfra.context.MinimalContext

 interface CheckDeps {

}

 interface MinimalCheckDeps : MinimalContext, CheckDeps {

    val mc: MinimalContext
    override val debug: Boolean
        get() = mc.debug
}

fun CheckDeps(mc: MinimalContext): MinimalCheckDepsImpl = MinimalCheckDepsImpl(mc)
data class MinimalCheckDepsImpl(override val mc: MinimalContext) : MinimalCheckDeps, MinimalContext by mc, CheckDeps {
    override val debug: Boolean
        get() = mc.debug
}

val MinimalContext.checkDeps: MinimalCheckDepsImpl
    get() = CheckDeps(this)
