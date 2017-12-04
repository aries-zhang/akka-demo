package com.example

import akka.actor.{Actor, ActorLogging, Props}

object MasterActor {
    def props(): Props = Props(new MasterActor())
}

class MasterActor extends Actor with ActorLogging {
    override def preStart(): Unit = {
        context.actorOf(SupervisorActor.props(), Constants.SUPERVISOR_ACTOR_NAME)
        context.actorOf(MonitorActor.props(), Constants.MONITOR_ACTOR_NAME)

        log.info("master is now running")
    }

    override def postStop(): Unit = log.info("master stopped")

    override def receive: Receive = Actor.emptyBehavior
}