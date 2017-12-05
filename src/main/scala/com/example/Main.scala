package com.example

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import com.example.MasterActor.KickOff

object Main extends App {
    val system = ActorSystem("example")

    try {
        // Start the shard for device actors
        ClusterSharding(system).start(
            typeName = DeviceActor.shardName,
            entityProps = DeviceActor.props(),
            settings = ClusterShardingSettings(system),
            extractShardId = DeviceActor.extractShardId,
            extractEntityId = DeviceActor.extractEntityId
        )

        //Start master as a singleton actor
        system.actorOf(ClusterSingletonManager.props(MasterActor.props(), PoisonPill, ClusterSingletonManagerSettings(system)), Constants.MASTER_ACTOR_NAME)

        println("The system is running..")

        while (true) {
            Thread.sleep(5000)
        }
    }
    catch {
        case ex: Throwable => println(s"unknown exception: ${ex.getMessage}")
    }
    finally {
        println("finally, the system is going to terminate")
        system.terminate()
    }
}