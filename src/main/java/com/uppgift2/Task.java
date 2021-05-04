package com.uppgift2;



public class Task {
    private long number = 0;

    //set number to 1000
    public void setNumber(){
        for(int i = 0; i<100000; i++) {
            number = i;
        }
        number = number/100;
    }

    public void consume(){
        System.out.println(findPrimes());
    }

    private long findPrimes() {
        long counter = 0;
        boolean isDividable = false;
        for(int i = 0; i<=number; i++) {
            for (long d = 2; d <= Math.sqrt(number); d++) {
                if (i % d == 0)
                    isDividable = true;
            }
            if(isDividable == false)
                counter++;
            isDividable = false;
        }
        return counter;
    }
}
