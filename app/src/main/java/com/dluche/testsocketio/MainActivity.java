package com.dluche.testsocketio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

import static com.github.nkzawa.emitter.Emitter.Listener;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    private EditText etInput;
    private Button btnAction;
    private Listener onNewMessage;
    private TextView tv_chat;


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
        onNewMessage = new Listener() {
            @Override
            public void call(final Object... args) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MySocket", "Chamou listner de recebeu msg websocket");
                        String username = "";
                        String message = "";
                        try {
                            if(args[0] instanceof JSONObject  ) {
                                JSONObject data = (JSONObject) args[0];
                                username = data.getString("username");
                                message = data.getString("message");
                            }else if(args[0] instanceof String){
                                username = "Anonimo";
                                message = (String) args[0];
                            }


                           // username = data.getString("username");
                            //message = data.getString("message");
                        } catch (Exception e) {
                            Log.d("MySocket", "execption no listner: " + e.toString());
                            return;
                        }

                        // add the message to view
                        addMessage(username, message);
                    }
                });

            }

        };
        //
        try {
            mSocket = IO.socket("https://chat.namoadigital.com");
        } catch (URISyntaxException e) {
            Log.d("MySocket", e.toString());
            //
            e.printStackTrace();
        }
    }

    private void addMessage(String username, String message) {
        Log.d("MySocket", "addMessage|Add msg de" +  username +" ->" + message);
        String chatHistory = tv_chat.getText().toString().trim();
        chatHistory += '\n' +username+ " disse:\n" + message;
        tv_chat.setText(chatHistory);
    }


    private void iniAction() {
        //                         \/ listner de msg enviada pelo server
        mSocket.on("chat message", onNewMessage);
        mSocket.connect();
        //
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etInput.getText().toString().trim().length() > 0){
                    attemptSend();
                }else{
                    Toast.makeText(getBaseContext(),"Digite algo",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void attemptSend() {
        String message = etInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        Log.d("MySocket", "attemptSend|Tentou enviar msg : "+ message);
        etInput.setText("");
        mSocket.emit("chat message", message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("chat message", onNewMessage);
        Log.d("MySocket", "Finalizou o chat");
    }



}
