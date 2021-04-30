package com.uppgift2;


import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Main2 {
    public static void main(String[] args) {
        long timeBeforeSetup = System.nanoTime();
        //#actor-system

        ActorSystem<MainActor2.Command> mainActor = ActorSystem.create(MainActor2.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new MainActor2.SetNumberOfTasksAndNumberOfWorkers(1000000, 8, timeBeforeSetup));
        //tell it to start
        mainActor.tell(MainActor2.Start.INSTANCE);
        //#main-send-messages

    }
}