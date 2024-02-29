package ai.chatinfra.ai.chatinfra.server

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
@Polymorphic
 sealed class Arch {
     abstract val name: String

    @Serializable
     object Arm : Arch() {
        override val name: String
            get() = "arm64"
    }

    @Serializable
     object X86 : Arch() {
        override val name: String
            get() = "amd64"
    }

     companion object {
         fun fromString(s: String): Arch = when (s) {
            Arm.name -> Arm
            X86.name -> X86
            "x86_64" -> X86
            else -> TODO(s)
        }
    }
}