package ai.chatinfra.ai.chatinfra.xfce

import XfceConf
import XfcePanel
import affair.lib.util.time.epoch
import ai.chatinfra.ai.chatinfra.context.systemcall.SystemCallResult
import ai.chatinfra.context.systemcall.SystemCallContext
import ai.chatinfra.ai.chatinfra.util.Res
import ai.chatinfra.bashscript.Bash


suspend inline fun SystemCallContext.createPanel(panel: XfcePanel) {
    val conf = XfceConf("xfce4-panel", this@createPanel)
    putPanelConf(panel.plugins.map { createPlugin(it, conf) }, conf)
}


suspend inline fun SystemCallContext.putPanelConf(pluginIds: List<Int>, conf: XfceConf): Int {
    conf.run {
        val panelId = panelsIds().max() + 1
        putInt("/panels/panel-$panelId/icon-size", 0, true)
        putInt("/panels/panel-$panelId/length", 40, true)
        putInt("/panels/panel-$panelId/size", 55, true)
        putBoolean("/panels/panel-$panelId/position-locked", value = false, create = true)
        putString("/panels/panel-$panelId/position", "p=0;x=0;y=0", true)
        putIntArray("/panels/panel-$panelId/plugin-ids", pluginIds, true)
        putIntArray("/panels", panelsIds())
        return panelId
    }
}

suspend inline fun SystemCallContext.createPlugin(plugin: XfcePlugin, conf: XfceConf): Int {
    log("creating plugin $plugin $conf")
    conf.run {
        val pluginId = plugins()
            .filterNot { it.name.contains("clipman") }
            .maxOfOrNull { it.id }!! + 1
        putString("/plugins/plugin-$pluginId", plugin.name, true)
        when (plugin) {
            is Launcher -> {
                val filename = "$epoch-${plugin.name}.desktop"
                putStringArray("/plugins/plugin-$pluginId/items", listOf(filename), true)
                plugin.content.let { writeFile("$home/.config/xfce4/panel/${plugin.name}-${pluginId}/$filename", it) }
            }

            else -> plugin.content?.let { writeFile("$home/.config/xfce4/panel/${plugin.name}-${pluginId}.rc", it) }
        }

        return pluginId
    }
}

suspend inline fun SystemCallContext.writeFile(filename: String, content: String): Res<SystemCallResult> = execBash(Bash { writeFile(filename, content) })

private suspend fun XfceConf.resetAndCreate() {

    val plugins = plugins()
    val panelKey = "panel-${panelsIds().max() + 1}"
    val selectedPluginIds = listOf(
        "cpugraph",
        "diskperf",
        "netload",
        "systemload",
        "separator",
        "notification-plugin",
        "separator",
        "systray"
    ).map { pluginName -> plugins.first { it.name == pluginName }.id }

    putInt("/panels/$panelKey/icon-size", 0, true)
    putInt("/panels/$panelKey/length", 40, true)
    putInt("/panels/$panelKey/size", 55, true)
    putBoolean("/panels/$panelKey/position-locked", false, true)
    putString("/panels/$panelKey/position", "p=0;x=0;y=0", true)
    putIntArray("/panels/$panelKey/plugin-ids", selectedPluginIds, true)
    putIntArray("/panels", panelsIds())
}

