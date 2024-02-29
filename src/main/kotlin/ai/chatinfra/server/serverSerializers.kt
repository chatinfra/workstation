package ai.chatinfra.ai.chatinfra.server

import kotlinx.serialization.modules.SerializersModule

 val serverSerializers: SerializersModule = SerializersModule {}
//    polymorphic(ServiceControl::class, SystemdServiceControl::class, SystemdServiceControl.serializer())
//    polymorphic(LogFile::class) {
//        subclass(DnsQueryLogFile::class, DnsQueryLogFile.serializer())
//        subclass(NfLogFile::class, NfLogFile.serializer())
//
//    }
//    polymorphic(Disk::class) {
//        subclass(FdiskEntry::class, FdiskEntry.serializer())
//    }
//    polymorphic(AllowTcpForwarding::class) {
//        subclass(AllowTcpForwarding.Local::class, AllowTcpForwarding.Local.serializer())
//        subclass(AllowTcpForwarding.No::class, AllowTcpForwarding.No.serializer())
//    }
//    polymorphic(App::class) {
//        subclass(DnsCrypt::class, DnsCrypt.serializer())
//        subclass(Sshd::class, Sshd.serializer())
//        subclass(Rsyslog::class, Rsyslog.serializer())
//        subclass(Ulogd2::class, Ulogd2.serializer())
//        subclass(Git::class, Git.serializer())
//        subclass(Ufw::class, Ufw.serializer())
//        subclass(Dhclient::class, Dhclient.serializer())
//    }
//    polymorphic(Endpoint::class) {
//        subclass(SshEndpoint::class, SshEndpoint.serializer())
//        subclass(RsyslogEndpoint::class, RsyslogEndpoint.serializer())
//    }
//    polymorphic(Server::class) {
//        subclass(SshServer::class, SshServer.serializer())
//    }
//    polymorphic(InstanceType::class) {
//        subclass(InstanceType.Pi4::class, InstanceType.Pi4.serializer())
//        subclass(InstanceType.Pixel5a::class, InstanceType.Pixel5a.serializer())
//    }
//    polymorphic(BaseApps::class) {
//        subclass(BaseAppsImpl::class, BaseAppsImpl.serializer())
//    }
//} + infraSerializers
