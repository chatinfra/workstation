package affair.lib.util.time

import kotlinx.datetime.Clock.System.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


 val Int.day: Duration
    get() = days
 val Int.second: Duration
    get() = seconds

 val epoch: Long
    get() = now().epochSeconds
 val epochMillis: Long
    get() = now().toEpochMilliseconds()
 val epochNanos: Long
    get() = now().run { epochSeconds + nanosecondsOfSecond }


 val epochSeconds: Long
    get() = now().epochSeconds


val Int.minute: Duration
    get() = minutes

val Int.hour: Duration
    get() = hours

 fun List<Duration>.combine(): Duration {
    var r = 0.minutes
    onEach { r += it }
    return r
}
