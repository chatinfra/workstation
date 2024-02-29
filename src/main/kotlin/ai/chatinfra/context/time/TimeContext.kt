package ai.chatinfra.ai.chatinfra.context.time

import affair.lib.util.time.day
import affair.lib.util.time.setDay
import affair.lib.util.time.setMidnight
import affair.lib.util.time.setMinute2
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds


 interface TimeContext {
     val tz: TimeZone
     val now: Instant


     fun LocalDateTime.setDay(day: DayOfWeek): LocalDateTime = setDay(day, tz)
     fun LocalDateTime.nextDay(): LocalDateTime = (instant + 1.day).local
     fun LocalDateTime.setMinute(minute: Int): LocalDateTime = setMinute2(minute)
     fun Instant.toLocalDateTime(): LocalDateTime = toLocalDateTime(tz)
     fun Instant.toDays(to: Instant): List<LocalDate> = mutableListOf<LocalDate>().apply {
        var ts = this@toDays
        while (ts < to) {
            add(ts.toLocalDateTime().date)
            ts += 1.day
        }
    }

    fun LocalDate.toPromptDateString(): String = "$dayOfWeekLowerCapped $this"


     fun LocalDate.atStartOfDay(): LocalDateTime = atStartOfDayIn(tz).toLocalDateTime()
     fun LocalDate.atEndOfDay(): LocalDateTime = (atStartOfDayIn(tz) + 24.hours - 1.seconds).toLocalDateTime()
     fun LocalDate.atStartOfDayInstant(): Instant = atStartOfDayIn(tz)
     fun LocalDate.atEndOfDayInstant(): Instant = atEndOfDay().toInstant(tz)

     val tomorrowStartOfLocalInstant: Instant
        get() = (now.local.setMidnight().instant + 1.day)
     val tomorrowStartOfLocal: LocalDateTime
        get() = (now.local.setMidnight().instant + 1.day).local
     val nextMonday: Instant
        get() = now.local
            .setMidnight()
            .setDay(java.time.DayOfWeek.MONDAY).instant
     val nextTuesday: Instant
        get() = now.local
            .setMidnight()
            .nextDay()
            .setDay(java.time.DayOfWeek.TUESDAY).instant

     val yesterday: Instant
        get() = (now.local.setMidnight().instant - 1.day)
     val dayOfTomorrow: DayOfWeek
        get() = (now + 1.day).toLocalDateTime(tz).dayOfWeek
     val dayOfDayAfterTomorrow: DayOfWeek
        get() = (now + 2.day).toLocalDateTime(tz).dayOfWeek


     val LocalDate.dayOfWeekLowerCapped: String
        get() = dayOfWeek.name.lowercase().cap
     val LocalDateTime.dayOfWeekLowerCapped: String
        get() = date.dayOfWeekLowerCapped
     val Instant.local: LocalDateTime
        get() = toLocalDateTime()
     val LocalDateTime.instant: Instant
        get() = toInstant(tz)

     operator fun LocalDateTime.plus(day: Duration): LocalDateTime = (toInstant(tz) + day).toLocalDateTime(tz)

     operator fun LocalTime.minus(other: LocalTime): Duration {
        val date = now.local.date
        return LocalDateTime(date, this).instant - LocalDateTime(date, other).instant
    }

     fun maxInstant(i1: Instant, i2: Instant): Instant = max(i1.toEpochMilliseconds(), i2.toEpochMilliseconds()).run { Instant.fromEpochMilliseconds(this) }
     fun minInstant(i1: Instant, i2: Instant): Instant = min(i1.toEpochMilliseconds(), i2.toEpochMilliseconds()).run { Instant.fromEpochMilliseconds(this) }

     companion object {
         fun createRealtime(tz: TimeZone = TimeZone.currentSystemDefault(), getNow: () -> Instant = { now() }): TimeContext = Realtime(tz = tz, getNow = getNow)
         fun createJanFirst2025(tz: TimeZone = TimeZone.currentSystemDefault()): TimeContext = Static(tz = tz, now = LocalDateTime(2025, 1, 1, 0, 0).toInstant(tz))
        fun createStatic(): TimeContext = Static(tz = TimeZone.currentSystemDefault(), now = now())
        fun createFromInstant(i: Instant): TimeContext = Static(tz = TimeZone.currentSystemDefault(), now = i)
    }

     data class Static(override val tz: TimeZone = TimeZone.currentSystemDefault(), override val now: Instant) : TimeContext

     data class Realtime(override val tz: TimeZone = TimeZone.currentSystemDefault(), val getNow: () -> Instant = { now() }) : TimeContext {

        override val now: Instant
            get() = getNow()
    }

}


 val String.cap: String
    get() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

 val String.decap: String
    get() = replaceFirstChar { it.lowercase(Locale.getDefault()) }