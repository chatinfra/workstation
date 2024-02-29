package ai.chatinfra.ai.chatinfra.net

import ai.chatinfra.ai.chatinfra.server.Hostname

 val Ip.slash24Cidr: Cidr
    get() = Cidr("${this.addr.substringBeforeLast(".")}.0/24")
 val Ip.cidr: Cidr
    get() = Cidr("${this.addr}/32")
 val Ip.hostname: Hostname
    get() = Hostname(this.addr)


 data class IpPortProto(
     val ip: Ip,
     val port: Int,
     val proto: Proto
)

