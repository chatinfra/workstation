package ai.chatinfra.ai.chatinfra.net

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable

 sealed class Proto {
     abstract val code: Int
     abstract val ipProtocol: String

    @kotlinx.serialization.Serializable
     object Icmp : Proto() {
        override val code: Int = 1
        override fun toString(): String = "icmp"
        override val ipProtocol: String = this::class.simpleName.toString()
    }

    @kotlinx.serialization.Serializable
     object Tcp : Proto() {
        override val code: Int = 6
        override fun toString(): String = "tcp"
        override val ipProtocol: String = this::class.simpleName.toString()
    }

    @kotlinx.serialization.Serializable

     object Udp : Proto() {
        override val code: Int = 17
        override fun toString(): String = "udp"
        override val ipProtocol: String = this::class.simpleName.toString()
    }

    @Serializable

     object Any : Proto() {
        override val code: Int = -1
        override fun toString(): String = "any"
        override val ipProtocol: String = "-1"
    }

     companion object {
         fun fromString(s: String): Proto = when (s) {
            "tcp" -> Tcp
            "6" -> Tcp
            "udp" -> Udp
            "17" -> Udp
            "icmp" -> Icmp
            "any" -> Any
            "-1" -> Any
            else -> TODO(s)
        }

         fun fromInt(i: Int): Proto = when (i) {
            6 -> Tcp
            17 -> Udp
            -1 -> Any
            1 -> Icmp
            else -> TODO(i.toString())
        }
    }
}