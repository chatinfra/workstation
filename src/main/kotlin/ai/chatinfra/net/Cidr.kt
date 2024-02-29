package ai.chatinfra.ai.chatinfra.net

import kotlin.random.Random

 data class Cidr( val block: String)  {
     override fun toString(): String = block

     companion object {
         val OPEN_MASK: Cidr = Cidr("0.0.0.0/0")
        private val ro: Int
            get() = Random.nextInt(0, 255)

         val randomLocal24: Cidr
            get() = Cidr("10.$ro.$ro.0/24")

         val randomPublic24: Cidr
            get() = Cidr("8.$ro.$ro.0/24")

         val randomLocal30: Cidr
            get() = Cidr("10.$ro.$ro.0/30")

         fun of(s: String): Cidr {
            return when {
//                s.contains(":") -> Ip(s.substringBeforeLast("/"))
                else -> Cidr(s)
            }
        }
    }
}

 val Cidr.slash30: Cidr
    get() = Cidr(block.substringBeforeLast("/") + "/30")
 val Cidr.slash24: Cidr
    get() = Cidr(block.substringBeforeLast("/") + "/30")