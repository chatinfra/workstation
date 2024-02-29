package affair.lib.util.time

import kotlinx.datetime.TimeZone

 val TimeZone.Companion.EASTERN: TimeZone
    get() = TimeZone.of("US/Eastern")