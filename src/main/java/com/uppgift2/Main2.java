package com.uppgift2;

import akka.actor.typed.ActorSystem;

public class Main2 {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        int numberOfTasks = 200000;
        int noProducers = 4;
        int noConsumers = 4;

        //#actor-system
        ActorSystem<MainActor2.Command> mainActor = ActorSystem.create(MainActor2.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new MainActor2.SetNumberOfTasksAndNumberOfWorkers(numberOfTasks, noProducers, noConsumers, timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor2.Start.INSTANCE);
        //#main-send-messages

    }
}