package com.example

import akka.actor.ActorSystem

object Main extends App {
    val system = ActorSystem("example")

    try {
        system.actorOf(MasterActor.props(), Constants.MASTER_ACTOR_NAME)

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