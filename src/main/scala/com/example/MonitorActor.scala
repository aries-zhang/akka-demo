package com.example

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import com.example.DeviceActor.Initiate

import scala.util.Random

object MonitorActor {
    def props(supervisor: ActorRef): Props = Props(new MonitorActor(supervisor))

    case class CheckDeviceInventoryMessage()
}

class MonitorActor(supervisor: ActorRef) extends Actor with ActorLogging {

    import MonitorActor._
    import context._

    var scheduler: Option[Cancellable] = None

    override def preStart(): Unit = {
        val scheduler = context.system.scheduler.schedule(10.second, 10.seconds, self, CheckDeviceInventoryMessage())
        this.scheduler = Some(scheduler)
        log.info("monitor is now running")
    }

    override def postStop(): Unit = {
        this.scheduler.foreach(_.cancel())
        this.scheduler = None
        log.info("monitor stopped")
    }

    override def receive: Receive = {
        case CheckDeviceInventoryMessage() => {
            log.info("time to check device inventory")

            this.fakeData().foreach(id => supervisor ! Initiate(id))
        }

        case _ => log.warning(s"unknown message from ${sender().path}")
    }

    def fakeData(): Array[Long] = 1.until(2).map(_ => Random.nextInt(1000).toLong).toArray
}
