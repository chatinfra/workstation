package ai.chatinfra.ai.chatinfra.server

import kotlinx.serialization.Serializable

 interface InstanceType {
     object c6gMedium : InstanceType {
        override val arch: Arch
            get() = Arch.Arm
        override val vcpu: Int
            get() = 1
        override val ramMb: Int
            get() = 2
        override val ssd: Boolean
            get() = false
    }

    @Serializable
     object Pi4 : InstanceType {
        override val arch: Arch
            get() = Arch.Arm
        override val vcpu: Int
            get() = 4
        override val ramMb: Int
            get() = 2
        override val ssd: Boolean
            get() = false
    }

    @Serializable
     object Fake : InstanceType {
        override val arch: Arch
            get() = Arch.Arm
        override val vcpu: Int
            get() = 0
        override val ramMb: Int
            get() = 0
        override val ssd: Boolean
            get() = false
    }

    @Serializable
     object Pixel5a : InstanceType {
        override val arch: Arch
            get() = Arch.Arm
        override val vcpu: Int
            get() = 8
        override val ramMb: Int
            get() = 6
        override val ssd: Boolean
            get() = true
    }

     val name: String
        get() = this::class.simpleName!!
     val arch: Arch
     val vcpu: Int
     val ramMb: Int
     val ssd: Boolean
}

