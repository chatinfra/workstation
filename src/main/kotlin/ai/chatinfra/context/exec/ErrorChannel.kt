package ai.chatinfra.ai.chatinfra.context.exec

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

//
interface ErrorChannel {
    val isActive: Boolean
    val isEmpty: Boolean
    var hasErrors: Boolean
    fun send(throwable: Throwable, message: String = "") = trySend(throwable, message)
    fun trySend(throwable: Throwable, message: String = "")
    val main: CoroutineContext

    data class Event(val throwable: Throwable, override val message: String = "") : Throwable() {
        override fun toString(): String {
            return "ErrorChannel.Event(message='$message', throwable=$throwable)"
        }
    }
    suspend fun close()
    suspend fun receiveAsFlow(function: (Event) -> Unit)
}

fun ErrorChannel(
    main: CoroutineContext,
    errors: Channel<ErrorChannel.Event> = Channel(UNLIMITED)
): ErrorChannel = ErrorChannelImpl(errors, main)

 class ErrorChannelImpl(
     val channel: Channel<ErrorChannel.Event>,
     override val main: CoroutineContext,
) : ErrorChannel {
    override val isEmpty: Boolean
        get() = channel.isEmpty
    override var hasErrors: Boolean = false
    @OptIn(DelicateCoroutinesApi::class)
    override val isActive: Boolean
        get() =!channel.isClosedForReceive

    override fun trySend(throwable: Throwable, message: String) {
        hasErrors = true

        CoroutineScope(main).launch {
            channel.trySend(ErrorChannel.Event(throwable, message))
                .onFailure { println("$channel ERRORCHANNEL trySend failed $it") }
        }
    }


    private fun printLine(s: String) {
        println("ErrorChannel $s  ")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun close() {
        channel.close()
        while (!channel.isClosedForReceive) delay(100.milliseconds)
    }

    override suspend fun receiveAsFlow(function: (ErrorChannel.Event) -> Unit) {
        channel.receiveAsFlow().collect { function(it) }
    }

//    override fun toString(): String {
//        return "ErrorChannel(isEmpty=$isEmpty, hasErrors=$hasErrors)"
//    }
}
