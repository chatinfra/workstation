package ai.chatinfra

import Xfce
import XfcePanel
import ai.chatinfra.ai.chatinfra.context.runBlockingSystemCall
import ai.chatinfra.ai.chatinfra.xfce.CpuGraph
import ai.chatinfra.ai.chatinfra.xfce.DiskPerf
import ai.chatinfra.ai.chatinfra.xfce.Launcher
import ai.chatinfra.ai.chatinfra.xfce.NetLoad
import ai.chatinfra.ai.chatinfra.xfce.NotificationPlugin
import ai.chatinfra.ai.chatinfra.xfce.Separator
import ai.chatinfra.ai.chatinfra.xfce.SystemLoad
import ai.chatinfra.ai.chatinfra.xfce.Systray
import ai.chatinfra.ai.chatinfra.xfce.disableDesktopIcons
import ai.chatinfra.ai.chatinfra.xfce.createPanel
import ai.chatinfra.ai.chatinfra.util.throwing
import ai.chatinfra.bashscript.Bash

fun main(args: Array<String>): Unit = runBlockingSystemCall(debug = true) {

    val xfce = Xfce()
    execBash(Bash {
        aptUpdate()
        aptInstall(xfce.requiredAptPackages())
        xfce.removeShortcuts.onEach { exec("xfconf-query -c xfce4-keyboard-shortcuts -p '$it' -r") }
        xfce.addShortcuts.onEach { exec("xfconf-query -c xfce4-keyboard-shortcuts -n -t 'string' -p '${it.key}' -s ${it.cmd} --create") }
    }).throwing()

    disableDesktopIcons()

    createPanel(XfcePanel(listOf(CpuGraph(), DiskPerf(), NetLoad(), SystemLoad(), NotificationPlugin(), Separator(), Systray(), Launcher())))
}


