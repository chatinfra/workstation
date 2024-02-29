package ai.chatinfra.ai.chatinfra.context.exec

import kotlinx.coroutines.channels.Channel

 sealed class ExecStdout {
     data class ToChannel(val c: Channel<String>) : ExecStdout()
     object ToResponse : ExecStdout()
     object Drop : ExecStdout()

     val collect: Boolean
        get() = this is ToResponse
}
