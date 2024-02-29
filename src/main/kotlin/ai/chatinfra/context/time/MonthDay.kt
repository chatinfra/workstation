package affair.lib.util.time

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
 data class MonthDay(
    val dayOfMonth: Int,
    val monthOfYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
    val year: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
) {

     fun render(): String = "$monthOfYear/$dayOfMonth/$year"

     companion object {
         fun parse(s: String): MonthDay = s.lowercase().replace("th", "").run {
            when {
                split("/").size == 2 -> split("/").run { MonthDay(last().toInt(), first().toInt()) }
                split("/").size == 3 -> split("/").run { MonthDay(get(1).toInt(), first().toInt(), last().toInt()) }
                split("-").size == 3 -> split("-").run { MonthDay(last().toInt(), get(1).toInt(), first().toInt()) }
                else -> MonthDay(toInt())
            }
        }

         fun parse(i: Instant): MonthDay = i.toLocalDateTime(TimeZone.currentSystemDefault()).run {
            MonthDay(dayOfMonth, monthNumber)
        }

         fun parse(i: LocalDateTime): MonthDay = i.run {
            MonthDay(dayOfMonth, monthNumber)
        }
    }
}

 fun LocalDateTime.toMonthDayHour(): MonthDayHour = run {
    MonthDayHour(hour, dayOfMonth, monthNumber)
}


 fun LocalDateTime.toMonthDay(): MonthDay = run {
    MonthDay(dayOfMonth, monthNumber)
}