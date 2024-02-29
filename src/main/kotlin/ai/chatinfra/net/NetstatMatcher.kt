package ai.chatinfra.ai.chatinfra.net

 sealed class NetstatMatcher {

     abstract fun compareTo(other: Endpoint): Boolean

     data class Exact(val endpoint: Endpoint) : NetstatMatcher() {
        override fun compareTo(other: Endpoint): Boolean = endpoint.ip == other.ip && endpoint.port == other.port && endpoint.proto == other.proto
    }

     data class PortOnly(val endpoint: Endpoint) : NetstatMatcher() {
        override fun compareTo(other: Endpoint): Boolean = endpoint.port == other.port
    }

     data class AlternateBind(val endpoint: Endpoint.AlternateBind) : NetstatMatcher() {
        override fun compareTo(other: Endpoint): Boolean = endpoint.bindIp == other.ip && endpoint.port == other.port && endpoint.proto == other.proto

    }
}