package affair.lib.util.time

import kotlinx.datetime.*

 fun Instant.setMidnightUtc(): Instant = (toString().substringBefore("T") + "T00:00:00.000000000Z")
    .run { Instant.parse(this) }

 fun Instant.setZeroSeconds(): Instant = (toString().substringBeforeLast(":") + ":00.000000000Z")
    .run { Instant.parse(this) }

 fun Instant.setZeroMilliseconds(): Instant = when {
    toString().substringBefore("Z").substringAfterLast(".").length == 9 -> Instant.parse(toString().substringBeforeLast(".") + ".000000000Z")
    //2023-12-30T01:31:23.272036Z
    toString().substringBefore("Z").substringAfterLast(".").length == 6 -> Instant.parse(toString().substringBeforeLast(".") + ".000000000Z")
    //2025-01-06T14:00:00Z
    toString().substringBefore("Z").substringAfterLast(":").length == 2 -> this
    else -> TODO(this.toString())
}

 fun LocalDateTime.setMidnight(): LocalDateTime = (toString().substringBefore("T") + "T00:00:00.000000")
    .run { LocalDateTime.parse(this) }

 fun LocalDateTime.setMinute2(i: Int): LocalDateTime = (toString().substringBeforeLast(":") + ":" + i.toString().padStart(2, '0') + ":00.000000")
    .run { LocalDateTime.parse(this) }

 fun LocalDateTime.setHour(i: Int): LocalDateTime = (toString().substringBefore("T") + "T" + i.toString().padStart(2, '0') + ":00:00.000000")
    .run { LocalDateTime.parse(this) }

 fun LocalDateTime.setDay(day: DayOfWeek, tz: TimeZone): LocalDateTime {
    var ts = toInstant(tz)

    while (ts.toLocalDateTime(tz).dayOfWeek != day) {
        ts += 1.day
    }
    return ts.toLocalDateTime(tz)

}