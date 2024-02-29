package ai.chatinfra.ai.chatinfra.xfce

import XfceConf
import ai.chatinfra.context.systemcall.SystemCallContext


suspend fun SystemCallContext.disableDesktopIcons() {
    XfceConf("xfce4-desktop",  this).apply {
        putBoolean("/desktop-icons/file-icons/show-filesystem", false, true)
        putBoolean("/desktop-icons/file-icons/show-home", false, true)
        putBoolean("/desktop-icons/file-icons/show-removable", false, true)
        putBoolean("/desktop-icons/file-icons/show-trash", false, true)
        putBoolean("/desktop-icons/file-icons/show-thumbnails", false, true)
        putBoolean("/desktop-icons/file-icons/show-tooltips", false, true)
    }
}
