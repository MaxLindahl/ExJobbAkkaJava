package com.uppgift1;


import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        //#actor-system
        ActorSystem<MainActor.Command> mainActor = ActorSystem.create(MainActor.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        //set how many numbers we will search and how many workers to spawn
        mainActor.tell(new MainActor.SetNumbersToSearchAndNumberOfWorkers(1000000, 8));
        //tell it to start
        mainActor.tell(MainActor.Start.INSTANCE);
        //#main-send-messages

        try {
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            mainActor.terminate();
        }
    }
}