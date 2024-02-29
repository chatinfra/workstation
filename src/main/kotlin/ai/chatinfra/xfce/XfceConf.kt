import ai.chatinfra.context.systemcall.SystemCallContext
import ai.chatinfra.ai.chatinfra.util.pmap
import ai.chatinfra.ai.chatinfra.util.throwing

data class XfceConf(val channel: String, val me: SystemCallContext) : SystemCallContext by me {

    suspend fun list(path: String? = null): List<String> {
        val pathCmd = path?.let { "-p '$path'" } ?: ""
        return execCmd("xfconf-query -c $channel -l $pathCmd".logIt("CMD")).throwing().lines
    }

    suspend fun plugins(): List<PanelPlugin> = list("/plugins")
        .map { it.substringAfter("/plugins/").substringBefore("/") }.distinct()
        .filterNot { it == "plugin-15" }
        .filterNot { it == "clipman" }
        .pmap { PanelPlugin(it.substringAfter("plugin-").toInt(), getString("/plugins/$it")) }

    suspend fun getString(property: String): String =
        execCmd("xfconf-query -c $channel -p $property").throwing().stdout

    suspend fun getIntArray(property: String): List<Int> =
        execCmd("xfconf-query -c $channel -p $property").throwing()
            .stdout.lines().run { subList(2, size) }.map { it.toInt() }

    suspend fun panelsIds(): List<Int> =
        execCmd("xfconf-query -c $channel -l -p /panels").throwing().stdout.lines()
            .filter { it.startsWith("/panels/panel") }
            .map { it.substringAfter("/panels/panel-").substringBefore("/") }.distinct().map { it.toInt() }

    suspend fun putEmpty(s: String) {
        execCmd("")
    }

    suspend fun putInt(key: String, value: Int, create: Boolean = false): Unit =
        execCmd("xfconf-query -c $channel -p $key ${if (create) " -n" else false} -t int -s $value").throwing()
            .run {}

    suspend fun putBoolean(key: String, value: Boolean, create: Boolean = false): Unit =
        execCmd("xfconf-query -c $channel -p $key ${if (create) " -n" else false} -t bool -s $value")
            .throwing()
            .run {}

    suspend fun putString(key: String, value: String, create: Boolean = false): Unit =
        execCmd("xfconf-query -c $channel -p $key ${if (create) " -n" else false} -t string -s '$value'")
            .throwing()
            .run {}

    suspend fun putIntArray(key: String, value: List<Int>, create: Boolean = false): Unit =
        execCmd("xfconf-query -c $channel -p $key ${if (create) " -n" else false} ${value.joinToString(" ") { "-t int -s $it" }} --force-array")
            .throwing().run {}

    suspend fun putStringArray(key: String, value: List<String>, create: Boolean = false): Unit =
        execCmd("xfconf-query -c $channel -p $key ${if (create) " -n" else false} ${value.joinToString(" ") { "-t string -s $it" }} --force-array")
            .throwing().run {}

    suspend fun reset(key: String): Unit =
        execCmd("xfconf-query -c $channel -p $key -r -R ").throwing().run { }

    data class PanelPlugin(val id: Int, val name: String)
}
