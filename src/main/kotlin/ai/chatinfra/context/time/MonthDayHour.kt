package affair.lib.util.time

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
 data class MonthDayHour(
    val hour: Int,
    val day: Int,
    val month: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
    val year: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
)