package com.dluche.testsocketio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    private EditText etInput;
    private Button btnAction;

    private Emitter.Listener onNewMessage;

    private TextView tv_chat;

    private SingletonWebSocket singletonWebSocket = SingletonWebSocket.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniVar();
        //
        iniAction();
    }


    private void iniVar() {
        //
        etInput = (EditText) findViewById(R.id.main_et_input_txt);
        //
        btnAction = (Button) findViewById(R.id.main_btn_action);
        //
        tv_chat = (TextView) findViewById(R.id.main_tv_chat);
        //
        singletonWebSocket.setOnISWS(new SingletonWebSocket.ISingletonWebSocket() {
            @Override
            public void chat(final String user, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessage(user, message);
                    }
                });
            }
        });

        //
//        onNewMessage = new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("MySocket", "Chamou listner de recebeu msg websocket");
//                        String username = "";
//                        String message = "";
//                        try {
//                            if (args[0] instanceof JSONObject) {
//                                JSONObject data = (JSONObject) args[0];
//                                username = data.getString("username");
//                                message = data.getString("message");
//                            } else if (args[0] instanceof String) {
//                                username = "Anonimo";
//                                message = (String) args[0];
//                            }
//
//
//                            // username = data.getString("username");
//                            //message = data.getString("message");
//                        } catch (Exception e) {
//                            Log.d("MySocket", "execption no listner: " + e.toString());
//                            return;
//                        }
//
//                        // add the message to view
//                        addMessage(username, message);
//                    }
//                });
//
//            }
//        };
//        //
//        try {
//
//            IO.Options options = new IO.Options();
//            options.reconnectionDelay = 0;
//            options.reconnectionAttempts = 1000;
//            //options.reconnection = true;
//
//            mSocket = IO.socket("https://chatdev.namoadigital.com", options);
//
//            mSocket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d("TT", "reconnect");
//                }
//            });
//
//
//            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d("TT", "timeout");
//                }
//            });
//
//            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    Log.d("TT", "DISCONNECT");
//                }
//            });
//
//        } catch (URISyntaxException e) {
//            Log.d("MySocket", e.toString());
//            //
//            e.printStackTrace();
//        }

    }

    private void addMessage(String username, String message) {
        Log.d("MySocket", "addMessage|Add msg de" + username + " ->" + message);
        String chatHistory = tv_chat.getText().toString().trim();
        chatHistory += '\n' + username + " disse:\n" + message;
        tv_chat.setText(chatHistory);
    }


    private void iniAction() {

        Intent mIntent = new Intent(getBaseContext(), AppBackgroundService.class);
        startService(mIntent);

        //        //                         \/ listner de msg enviada pelo server
//        mSocket.on("switchRoom", onNewMessage);
//        mSocket.on("message", onNewMessage);
//        mSocket.connect();
//
//        mSocket.emit("room", "sala1");
//        //
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etInput.getText().toString().trim().length() > 0) {
                    singletonWebSocket.attemptSend(etInput.getText().toString().trim());
                } else {
                    Toast.makeText(getBaseContext(), "Digite algo", Toast.LENGTH_SHORT).show();
                }
            }
//                if (etInput.getText().toString().trim().length() > 0) {
//                    attemptSend();
//                } else {
//                    Toast.makeText(getBaseContext(), "Digite algo", Toast.LENGTH_SHORT).show();
//                }
//
//            }
        });
    }

    private void attemptSend() {
        String message = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        Log.d("MySocket", "attemptSend|Tentou enviar msg : " + message);
        etInput.setText("");
        mSocket.emit("message", message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
