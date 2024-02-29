package ai.chatinfra.ai.chatinfra.net

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
@JvmInline
//@Ref("infra.Cidr", multiple = false) //There's a cycle in the inheritance hierarchy for this type
//@Relation(key = ["addr"],)
value class Ip(
    val addr: String
) {
    override fun toString(): String = addr

    companion object {
        fun of(s: String): Ip {
            return when {
//                s.contains(":") -> Ip(s.substringBeforeLast("/"))
                else -> Ip(s.substringBeforeLast("/"))
            }
        }

        val localhostIp: Ip = of("127.0.0.1")
        val OPEN_MASK: Ip = of("0.0.0.0")

        val random: Ip
            get() = Ip("10.$ro.$ro.$ro")
        private val ro: Int
            get() = Random.nextInt(0, 255)
    }
}