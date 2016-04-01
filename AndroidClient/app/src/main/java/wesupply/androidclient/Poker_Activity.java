/*
package com.example.jonas.te4client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

    private Socket socket;

    private static final int SERVERPORT = 6000;
    //Byt ip-adress -> mobilen som agerar server har en ip-adress, skriv den här nedan!
    private static final String SERVER_IP = "192.168.1.88";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new ClientThread()).start();
    }

    public void onClick(View view) {
        try {
            EditText et = (EditText) findViewById(R.id.EditText01);
            String str = et.getText().toString();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            out.println(str);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
}
*/

package wesupply.androidclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class Poker_Activity extends AppCompatActivity implements View.OnClickListener {
    private TextView clientcard1id;
    private TextView clientcard2id;
    private TextView nicknameTxtviewid;
    private Button checkButton;
    Client client;
    private int card1;
    private int card2;
    String ready = null;
    public static String ip_adress;
    public static String port_adress;
    public static String poker_nickname;


    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poker_activity);
        Intent intent = getIntent();

        client = new Client(ip_adress, Integer.parseInt(port_adress));

        String nickname = intent.getStringExtra(poker_nickname);
        TextView nicknameTxtid = (TextView)findViewById(R.id.nicknameTxtid);
        nicknameTxtviewid = (TextView) findViewById(R.id.nicknameTxtviewid);
        nicknameTxtid.setText(nickname);

        clientcard1id = (TextView) findViewById(R.id.clientcard1id);
        clientcard2id = (TextView) findViewById(R.id.clientcard2id);

        checkButton = (Button) findViewById(R.id.checkButton);

        checkButton.setOnClickListener(this);

        t = new Thread(new CommunicationThread());
        t.start();
    }

    //Print method used in UI thread
    void Print(String message){
        clientcard1id.setText(clientcard1id.getText().toString() + message + "\n");
    }

    //Test method, use method SafePrint below if everything works
    //NOT SAFE
    //void SafePrint(String message){
     //   text2.setText(text2.getText().toString() + message + "\n");
    //}


    //Print method DO NOT USE in ui thread
    //Thread safe print method
    void SafePrint(String out_text) {
        clientcard1id.post(new Printer(out_text));
    }

    class Printer implements Runnable{
        String out_text;
        Printer(String text){out_text = text;}

        public void run(){
            clientcard1id.setText(clientcard1id.getText().toString() + out_text + "\n");
        }
    }

    void SendAndRecieveData(){
        List<String> data = client.RecieveData();

        card1 = Integer.parseInt(data.get(0));
        card2 = Integer.parseInt(data.get(1));

        PrintCard(card1);
        PrintCard(card2);
        }


   public void PrintCard(int card) {
        int value_card = card % 13;

       if (value_card == 0){
           value_card = 13;
       }

        if (card > 0 && card < 14) {
            SafePrint("♥ " + value_card);
        }
        if (card > 13 && card < 27) {
            SafePrint("♠ " + value_card);
        }
        if (card > 26 && card < 40) {
            SafePrint("♣ " + value_card);
        }
        if (card > 39 && card < 53) {
            SafePrint("♦ " + value_card);
        }
    }

    public void onClick(View poker){

        switch(poker.getId()){

            case R.id.checkButton:
                ready = "client1rdy\n";
                client.SendData(ready);
                client.LetServerRead();
                clientcard2id.setText(clientcard2id.getText()+"Test\n");

        }
    }


    class CommunicationThread implements Runnable{
        public void run(){
            try {
                client.Connect();

                SendAndRecieveData();
            }
            catch(Exception e) {
                SafePrint(e.getMessage());
            }
        }
    }

    public class Client {
        private Socket socket;
        private String server_ip;
        private int server_port;
        private InetAddress server_address;
        private PrintWriter output;
        private BufferedReader input;

        public Client(String ip_address, int port_number) {
            InitVariables(ip_address, port_number);
        }

        private void InitVariables(String ip_address, int port_number) {
            socket = null;
            server_ip = ip_address;
            server_port = port_number;
            server_address = null;
            output = null;
            input = null;
        }

        public void Connect() {
            try {
                server_address = InetAddress.getByName(server_ip);
                socket = new Socket(server_address, server_port);

                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                output = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())), false);
            }
            catch (UnknownHostException e) {
                SafePrint("Server not found, check IP-adress");
            }
            catch (IllegalArgumentException e) {
                SafePrint("Port number out of bounds (must be between 0 and 65535)");
            }
            catch (Exception e) {
                SafePrint(e.getMessage());
            }
        }

        public void SendData(String ready) {
            try {
                if (output != null){
                    output.print(ready);
                }
            }
            catch (Exception e) {
                SafePrint(clientcard1id.getText() + (e.getMessage()));
            }
        }

        public void LetServerRead() {
            output.flush();
        }

        public List<String> RecieveData() {
            List<String> data_list = new ArrayList<String>();
            try {
                SafePrint("Client <- Server");

                String data = "";

                if (input != null){
                    data = input.readLine();
                    while (data.compareTo("_endofdata_") != 0) {
                        data_list.add(data);
                        data = input.readLine();
                    }
                }
            }
            catch(Exception e) {
                SafePrint(e.getMessage());
            }

            return data_list;
        }

        public void Disconnect(){
            try {
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
                SafePrint(e.getMessage());
            }
        }
    }
}