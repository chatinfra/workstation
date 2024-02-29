package ai.chatinfra.ai.chatinfra.net


import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

@Serializable
 data class ProxyEndpoint(val ifname: String, override val ip: Ip, override val port: Int) : Endpoint {
    override val proto: Proto
        get() = Proto.Tcp
}


 val FakeProxyHttp: Proxy.Http = Proxy.Http(Ip.localhostIp, 0)
 val FakeProxySocks: Proxy.Socks = Proxy.Socks(Ip.localhostIp, 0)


@Serializable
 sealed class Proxy : Endpoint {

     abstract val url: String
     abstract override val ip: Ip
     abstract override val port: Int

    @Serializable
     data class Http(override val ip: Ip, override val port: Int) : Proxy() {
        val host: String
            get() = ip.addr
        override val url: String
            get() = "http://${ip.addr}:$port"
        override val proto: Proto
            get() = Proto.Tcp

         companion object {
             fun parse(s: String): Http = when {
                s.startsWith("http://") -> Http(Ip(s.substringAfter("//").substringBefore(":")), s.substringAfterLast(":").toInt())
                else -> TODO("Proxy.Http.parse $s")
            }
        }
    }

    @Serializable
     data class Socks(override val ip: Ip, override val port: Int) : Proxy() {
        val host: String
            get() = ip.addr
        override val url: String
            get() = "socks5h://${ip.addr}:$port"
        override val proto: Proto
            get() = Proto.Tcp
    }


     companion object {
         val PROXY_ENV_KEYS: List<String> = listOf("HTTP_PROXY", "HTTPS_PROXY", "http_proxy", "https_proxy")

         fun parse(s: String): Proxy = when {
            s.startsWith("socks5h://") -> Socks(Ip(s.substringAfter("//").substringBefore(":")), s.substringAfterLast(":").toInt())
            s.startsWith("http://") -> Http(Ip(s.substringAfter("//").substringBefore(":")), s.substringAfterLast(":").toInt())
            else -> TODO(s)
        }
    }
}

 fun Proxy.toJavaNetProxy(): java.net.Proxy = when (this) {
    is Proxy.Http -> java.net.Proxy(java.net.Proxy.Type.HTTP, InetSocketAddress(ip.addr, port))
    is Proxy.Socks -> java.net.Proxy(java.net.Proxy.Type.SOCKS, InetSocketAddress(ip.addr, port))
}
