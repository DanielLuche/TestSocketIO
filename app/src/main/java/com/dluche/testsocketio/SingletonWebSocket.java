package com.dluche.testsocketio;

/**
 * Created by neomatrix on 24/11/17.
 */

public class SingletonWebSocket {

    //https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0

    private static volatile SingletonWebSocket sSoleInstance;

    //private constructor.
    private SingletonWebSocket() {

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletonWebSocket getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (SingletonWebSocket.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) {
                    sSoleInstance = new SingletonWebSocket();
                }
            }
        }

        return sSoleInstance;
    }
}
