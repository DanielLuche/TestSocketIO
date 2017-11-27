package com.dluche.testsocketio;

import android.util.Log;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by neomatrix on 24/11/17.
 */

public class SingletonWebSocket {

    //https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0

    private static volatile SingletonWebSocket sSoleInstance;

    private static int reconnectCount = 0;

    private boolean mSocketStatus = false;

    public Socket mSocket;

    private Emitter.Listener onNewMessage;

    public interface ISingletonWebSocket {
        void chat(String user, String message);
    }

    private ISingletonWebSocket delegate;

    public void setOnISWS(ISingletonWebSocket delegate) {
        this.delegate = delegate;
    }

    public void setmSocketStatus(boolean mSocketStatus) {
        this.mSocketStatus = mSocketStatus;
    }

    //private constructor.
    private SingletonWebSocket() {

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        try {
            initConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initConnection() {
        onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                String username = "";
                String message = "";
                try {
                    if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        username = data.getString("username");
                        message = data.getString("message");
                    } else if (args[0] instanceof String) {
                        username = "Anonimo";
                        message = (String) args[0];
                    }


                    // username = data.getString("username");
                    //message = data.getString("message");
                } catch (Exception e) {
                    Log.d("MySocket", "execption no listner: " + e.toString());
                    return;
                }

                if (delegate != null) {
                    delegate.chat(username, message);
                }

                ToolBox.writeFF("SingleTon", "lista.txt");

            }
        };

        IO.Options options = new IO.Options();
        options.timeout = 1000;

        try {
            mSocket = IO.socket("https://chatdev.namoadigital.com", options);

            mSocket.on("switchRoom", onNewMessage);
            mSocket.on("message", onNewMessage);
            mSocket.connect();

            mSocket.emit("room", "sala1");

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    reconnectCount = 0;
                }
            });
            mSocket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    reconnectCount = 0;
                }
            });

            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    disconnect(true);
                }
            });

            mSocket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        disconnect();
        initConnection();
    }

    public void disconnect() {
        disconnect(false);
    }

    public void disconnect(boolean reconectStatus) {
        if (mSocket != null) {
            mSocket.off();
            mSocket.disconnect();
            mSocket = null;
        }

        if (reconectStatus) {
            if (++reconnectCount < 3){
                initConnection();
            }
        } else {
            reconnectCount = 0;
        }
    }

    public void attemptSend(String message) {
        if (mSocket != null) {
            mSocket.emit("message", message);
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
