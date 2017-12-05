package com.example

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}

object DeviceActor {
    def props(): Props = Props(new DeviceActor())

    case class Initiate(deviceId: Long)

    def shardName = "device-region"

    val extractShardId: ExtractShardId = {
        case Initiate(deviceId) => (deviceId % 3).toString
    }

    val extractEntityId: ExtractEntityId = {
        case message: Initiate => (message.deviceId.toString, message)
    }
}

class DeviceActor() extends Actor with ActorLogging {

    import DeviceActor._

    val deviceId: Long = self.path.elements.last.split("-").last.toLong

    override def preStart(): Unit = log.info(s"device $deviceId is now running")

    override def postStop(): Unit = log.info(s"device $deviceId stopped")

    override def receive: Receive = {
        case Initiate(id) => log.info(s"I am initiated ^^$id^^")
        case _ => log.info(s"unknown message from ${sender()}")
    }
}
