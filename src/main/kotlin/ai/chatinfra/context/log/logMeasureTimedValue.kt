package ai.chatinfra.ai.chatinfra.context.log

import ai.chatinfra.context.LogContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.TimeSource
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

@OptIn(ExperimentalContracts::class)
 inline fun <T> LogContext.logMeasureTimedValue(name: String, block: () -> T): TimedValue<T> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    log("$name.start")
    return TimeSource.Monotonic.measureTimedValue(block).also { log("$name.end ${it.duration}") }
}