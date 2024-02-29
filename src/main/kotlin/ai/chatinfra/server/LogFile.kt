package ai.chatinfra.ai.chatinfra.server

import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames

interface LogFile {
    val absolute: String
    val name: String
        get() = absolute.substringAfterLast("/")

    fun decodeFile(lines: List<String>, json: Json): List<LogLine> = lines.mapNotNull { decodeLine(it, json) }.filter { filter(it) }

    fun filter(line: LogLine): Boolean = true

    fun decodeLine(line: String, json: Json): LogLine?
}


@Serializable
data class SyslogFile(
    val serviceName: String? = null,
    override val absolute: String = "/data/log/syslog.json",
) : LogFile {
    override fun filter(line: LogLine): Boolean = (line as SyslogLine).source == serviceName

    override fun decodeLine(line: String, json: Json): SyslogLine = json.decodeFromString(SyslogLine.serializer(), line)


}

@Serializable
data class SyslogLine @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("@timestamp") val timestamp: Instant,
    val host: String,
    val severity: Int,
    val facility: Int,
    @JsonNames("syslog-tag") val tag: String,
    val source: String,
    val message: String
) : LogLine

interface LogLine
