package com.example

import akka.actor.{Actor, ActorLogging, Props}

object DeviceActor {
    def props(deviceId: Long): Props = Props(new DeviceActor(deviceId))
}

class DeviceActor(deviceId: Long) extends Actor with ActorLogging {
    override def preStart(): Unit = log.info(s"device $deviceId is now running")

    override def postStop(): Unit = log.info(s"device $deviceId stopped")

    override def receive: Receive = {
        case _ => log.info(s"unknown message from ${sender()}")
    }
}
