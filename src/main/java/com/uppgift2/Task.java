package com.uppgift2;


import java.util.Random;

public class Task {
    private int number = 0;
    private int min = 100;
    private int max = 1000;

    public Task(){

    }

    public void setNumber(){
        for(int i = 0; i<100000; i++) {
            number = i;
        }
    }

    public void consume(){
        findFactorial();
    }

    private int findFactorial(){
        int factorial = 1;
        for(int i = 1; i <= number; i++){
            factorial = factorial * i;
        }
        return factorial;
    }
}
