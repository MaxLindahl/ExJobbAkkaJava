package com.uppgift3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("MultitaskerSystem");
        int noActors = 500;
        ActorRef[] actors = new ActorRef[noActors];

        for (int i = 0; i < noActors; i++) {
            actors[i] = actorSystem.actorOf(Props.create(MultitaskerActor.class));
            actors[i].tell(i, ActorRef.noSender());
        }
        for (int i = 0; i < noActors; i++) {
            actors[i].tell("work", ActorRef.noSender());
        }
        System.out.println("main thread done");
    }


}
