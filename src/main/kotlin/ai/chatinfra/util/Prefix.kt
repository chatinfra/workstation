package ai.chatinfra.ai.chatinfra.util

import kotlinx.serialization.Serializable

@Serializable
 data class Prefix(val labels: List<String>) {

     operator fun plus(s: String): Prefix = Prefix(*labels.toTypedArray() + s)
     operator fun plus(i: Int): Prefix = Prefix(*labels.toTypedArray() + i.toString())
     fun add(s: String): Prefix = Prefix(*labels.toTypedArray() + s)
     operator fun plus(l: Long): Prefix = Prefix(*labels.toTypedArray() + l.toString())

    override fun toString(): String {
        return "Prefix('$dotted')"
    }
     val name: String
        get() = dotted

     val vpcName: String
        get() = labels.last().uppercase()

    val dotted: String
        get() = labels.joinToString(".")

    //     val labels: List<String> = label.toList()
     companion object {
         fun fromDotted(s: String): Prefix = Prefix(s.split("."))
    }
}

 fun Prefix(vararg label: String): Prefix = Prefix(label.toList())
