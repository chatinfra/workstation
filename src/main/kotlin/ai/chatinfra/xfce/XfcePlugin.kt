package ai.chatinfra.ai.chatinfra.xfce

 interface XfcePlugin {
     val name: String
     val content: String?
        get() = null
}


 class Launcher() : XfcePlugin {
    override val name: String = "launcher"
    override val content: String
        get() = """
[Desktop Entry]
Name=Firefox ESR
Comment=Browse the World Wide Web
GenericName=Web Browser
X-GNOME-FullName=Firefox ESR Web Browser
Exec=/usr/lib/firefox-esr/firefox-esr %u
Terminal=false
X-MultipleArgs=false
Type=Application
Icon=firefox-esr
Categories=Network;WebBrowser;
MimeType=text/html;text/xml;application/xhtml+xml;application/xml;application/vnd.mozilla.xul+xml;application/rss+xml;application/rdf+xml;image/gif;image/jpeg;image/png;x-scheme-handler/http;x-scheme-handler/https;
StartupWMClass=Firefox-esr
StartupNotify=true
X-XFCE-Source=file:///usr/share/applications/firefox-esr.desktop
            
        """.trimIndent()
}


 class CpuGraph() : XfcePlugin {
    override val name: String = "cpugraph"
}

 data class DiskPerf(val dev: String = "/dev/sda") : XfcePlugin {
    override val name: String = "diskperf"
    override val content: String
        get() = """
Device=$dev
UseLabel=0
Text=sda
UpdatePeriod=500
Statistics=0
XferRate=40
CombineRWdata=1
MonitorBarOrder=0
ReadColor=rgb(0,0,255)
WriteColor=rgb(255,0,0)
ReadWriteColor=rgb(0,255,0)
        """.trimIndent()
}

 class NetLoad() : XfcePlugin {
    override val name: String = "netload"
    override val content: String
        get() = """
Use_Label=false
Show_Values=false
Show_Bars=true
Colorize_Values=false
Color_In=rgb(255,79,0)
Color_Out=rgb(255,229,0)
Text=Net
Network_Device=
Max_In=4096
Max_Out=4096
Auto_Max=true
Update_Interval=250
Values_As_Bits=false            
        """.trimIndent()
}

 class SystemLoad() : XfcePlugin {
    override val name: String = "systemload"
    override val content: String
        get() = """
[Main]
Timeout=500
Timeout_Seconds=1
Click_Command=xfce4-taskmanager

[SL_Cpu]
Enabled=false
Use_Label=false
Color=rgb(0,0,192)
Text=

[SL_Mem]
Enabled=true
Use_Label=false
Color=rgb(0,192,0)
Text=

[SL_Swap]
Enabled=true
Use_Label=false
Color=rgb(240,240,0)
Text=

[SL_Uptime]
Enabled=false
            
        """.trimIndent()

}

 class Separator() : XfcePlugin {
    override val name: String = "separator"
}

 class NotificationPlugin() : XfcePlugin {
    override val name: String = "notification-plugin"
}

 class Systray() : XfcePlugin {
    override val name: String = "systray"
}


