package com.uppgift3;

import akka.actor.UntypedAbstractActor;

public class MultitaskerActor extends UntypedAbstractActor {

    public int id;

    public void onReceive(Object message){
        if (message instanceof Integer)
            this.id = (int)message;
        if (message.equals("work"))
            doWork();
    }
    private void doWork(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Actor "+ id + " done");
    }


}
