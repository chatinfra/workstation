package ai.chatinfra.ai.chatinfra.net

import ai.chatinfra.ai.chatinfra.server.CertBundle
import ai.chatinfra.ai.chatinfra.server.Zone

interface Endpoint {
     val ip: Ip
     val port: Int
     val proto: Proto
     val hostPort: String
        get() = "${ip.addr}:$port"

//     interface Public : Endpoint {
//         val publicIp: Ip
//         val hostname: Hostname
//         val url: String
//            get() = if (port != 443) "https://$hostname" else "https://$hostname:$port"
//    }

     val matcher: NetstatMatcher
        get() = NetstatMatcher.Exact(this)

     interface HasHttpUrl : Endpoint {
         val httpUrl: String
            get() = "http://${ip.addr}" + if (port != 80) ":$port" else ""
    }

     interface HasName : Endpoint {
         val name: String
    }
     interface HasIface : Endpoint {
         val ifname: String
    }

     interface HasSsl : Endpoint {
         val hostname: String
    }
     interface HasCert : Endpoint {
         val cert: CertBundle
    }
     interface HasDns : Endpoint {
        val zone: Zone
    }

     interface HasProxy : Endpoint {
         val name: String
         fun proxy(wgIp: Ip): Proxy
    }

     interface AlternateBind : Endpoint {
         val bindIp: Ip
    }

     interface User : Endpoint {

    }

     companion object
}
