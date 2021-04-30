package com.uppgift1;


import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        //#actor-system
        ActorSystem<MainActor.Command> mainActor = ActorSystem.create(MainActor.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        //set how many numbers we will search and how many workers to spawn

        mainActor.tell(new MainActor.SetNumbersToSearchAndNumberOfWorkers(1000, 8, timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor.Start.INSTANCE);
        //#main-send-messages

    }
}