package com.example

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import com.example.SupervisorActor.RegisterDeviceMessage

object MonitorActor {
    def props(): Props = Props(new MonitorActor())

    case class CheckDeviceInventoryMessage()
}

class MonitorActor extends Actor with ActorLogging {
    import MonitorActor._
    import context._

    var scheduler: Option[Cancellable] = None

    override def preStart(): Unit = {
        val scheduler = context.system.scheduler.schedule(0.second, 30.seconds, self, CheckDeviceInventoryMessage())
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
            val supervisor = context.actorSelection(s"../${Constants.SUPERVISOR_ACTOR_NAME}")
            this.fakeData().foreach(id => supervisor ! RegisterDeviceMessage(id))
        }

        case _ => log.warning(s"unknown message from ${sender().path}")
    }

    def fakeData(): Array[Long] = Array(1, 2, 3, 4, 5, 6)
}
