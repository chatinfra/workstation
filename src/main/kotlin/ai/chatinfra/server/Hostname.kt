package ai.chatinfra.ai.chatinfra.server

import kotlinx.serialization.Serializable

@Serializable
 data class Hostname(val name: String) {

    override fun toString(): String = name
     fun first(): String = name.substringBefore(".")

     companion object {
         val localhostHostname: Hostname
            get() = Hostname("localhost")

         fun fromString(s: String): Hostname = Hostname(s)
    }
}


val Hostname.domain: String
    get()= name.split(".").run { subList(size - 2, size).joinToString(".") }
val Hostname.wildcard: Hostname
    get() = Hostname("*.$domain")