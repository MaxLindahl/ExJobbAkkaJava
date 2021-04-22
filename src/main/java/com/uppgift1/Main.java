package com.uppgift1;


import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class Main {
    public static void main(String[] args) {
        //#actor-system
        final ActorSystem<MainActor.Start> mainActor = ActorSystem.create(MainActor.create(), "mainActorSystem");
        //#actor-system

        //Is this correct??
        //#main-send-messages
        mainActor.tell(new MainActor.Start());
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