package com.example

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.cluster.sharding.ClusterSharding
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}


object MasterActor {
    def props(): Props = Props(new MasterActor())

    case class KickOff()
}

class MasterActor extends Actor with ActorLogging {
    override def preStart(): Unit = {
        // Start device guardian as a sharding region
        val deviceSupervisor = ClusterSharding(context.system).shardRegion(DeviceActor.shardName)
        log.info("supervisor is kicked off")

        // Start monitor as a singleton
        context.actorOf(ClusterSingletonManager.props(MonitorActor.props(deviceSupervisor), PoisonPill, ClusterSingletonManagerSettings(context.system)), Constants.MONITOR_ACTOR_NAME)
        log.info("monitor is kicked off")

        log.info("master is now running")
    }

    override def postStop(): Unit = log.info("master stopped")

    override def receive: Receive = Actor.emptyBehavior
}