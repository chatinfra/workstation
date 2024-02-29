import ai.chatinfra.ai.chatinfra.server.App
import ai.chatinfra.ai.chatinfra.xfce.XfcePlugin
import ai.chatinfra.ai.chatinfra.util.USER
import kotlinx.serialization.Serializable


@Serializable
class Xfce(
    val user: String = USER,
    val addShortcuts: List<KeyShortcut> = listOf(
        KeyShortcut("/xfwm4/custom/<Alt>grave", "switch_window_key"), KeyShortcut("/commands/custom/<Super>l", "xflock4")
    ),
    val removeShortcuts: List<String> = listOf("/commands/custom/<Primary><Alt>l"),
    val laptop: Boolean = true
) : App {



    fun requiredAptPackages(): List<String> = listOf(
        "dbus-x11",
        "xfce4-panel",
        "xfce4-session",
        "xfconf",
        "xfce4-notifyd",
        "xfce4-terminal",
        "xfce4-appfinder",
        "xfce4-taskmanager",
        "xfce4-screenshooter",
        "xfce4-clipman",
        "xfce4-cpugraph-plugin",
        "xfce4-diskperf-plugin",
        "xfce4-netload-plugin",
        "xfce4-systemload-plugin",
        "xfce4-pulseaudio-plugin",
        "hicolor-icon-theme",
        "thunar"
    ) + if (laptop) listOf(
        "xfce4-power-manager",
        "xfce4-power-manager-plugins"
    ) else emptyList()


}


data class XfcePanel(val plugins: List<XfcePlugin>) {
}
@Serializable
data class KeyShortcut(val key: String, val cmd: String)