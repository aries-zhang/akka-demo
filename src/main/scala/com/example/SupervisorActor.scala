package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object SupervisorActor{
    def props(): Props = Props(new SupervisorActor())

    case class RegisterDeviceMessage(deviceId: Long)
}

class SupervisorActor extends Actor with ActorLogging {

    import SupervisorActor._

    var deviceMap: Map[Long, ActorRef] = Map.empty

    override def preStart(): Unit = log.info("supervisor is now running")

    override def postStop(): Unit = log.info("supervisor stopped")

    override def receive: Receive = {
        case RegisterDeviceMessage(deviceId) => this.register(deviceId)
        case _ => log.warning(s"unknown message from ${sender().path}")
    }

    def register(deviceId: Long): Unit = {
        val exists = this.deviceMap.get(deviceId).isDefined
        log.info(s"device $deviceId ${if (exists) "" else "not"} exists")
        if (!exists) {
            this.deviceMap += (deviceId -> context.actorOf(DeviceActor.props(deviceId), s"dev-$deviceId"))
        }
    }
}
