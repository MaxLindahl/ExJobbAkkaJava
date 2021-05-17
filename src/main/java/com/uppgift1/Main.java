package com.uppgift1;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class Main {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        ConfigFactory.load("src/main/resources/application.conf");
        long numbersToSearch = 100000;
        int numberOfWorkers = 64;
        //#actor-system
        ActorSystem<MainActor.Command> mainActor = ActorSystem.create(MainActor.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        //set how many numbers we will search and how many workers to spawn
        mainActor.tell(new MainActor.SetNumbersToSearchAndNumberOfWorkers(numbersToSearch, numberOfWorkers, timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor.Start.INSTANCE);
        //#main-send-messages

    }
}