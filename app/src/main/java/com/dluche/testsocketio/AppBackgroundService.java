package com.dluche.testsocketio;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by neomatrix on 27/11/17.
 */

public class AppBackgroundService extends Service {

    public static boolean isRunning;

//    private Thread backgroundThread;
    private SingletonWebSocket singletonWebSocket;


    @Override
    public void onCreate() {
        this.isRunning = false;
    }

//    private Runnable myTask = new Runnable() {
//        @Override
//        public void run() {
//            try {
//
//                singletonWebSocket = SingletonWebSocket.getInstance();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!this.isRunning) {
//            this.isRunning = true;
//
//            //this.singletonWebSocket = SingletonWebSocket.getInstance();
//            this.backgroundThread = new Thread(myTask);
//
//            this.backgroundThread.start();
//        }

        ToolBox.writeFF("Service Started", "lista.txt");

        singletonWebSocket = SingletonWebSocket.getInstance();
        singletonWebSocket.setmSocketStatus(true);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        //
        ToolBox.writeFF("Service Stopped", "lista.txt");
        //
        singletonWebSocket.disconnect();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
