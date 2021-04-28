package com.uppgift2;


import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Main2 {
    public static void main(String[] args) {
        //#actor-system
        ActorSystem<MainActor2.Command> mainActor = ActorSystem.create(MainActor2.create(), "mainActorSystem");
        //#actor-system

        //#main-send-messages
        mainActor.tell(new MainActor2.SetNumberOfTasksAndNumberOfWorkers(1000000, 4));
        //tell it to start
        mainActor.tell(MainActor2.Start.INSTANCE);
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