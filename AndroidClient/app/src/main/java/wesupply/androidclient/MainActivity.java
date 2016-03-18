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

import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView text2;
    Client client;

    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new Client("192.168.43.158", 5500);

        text2 = (TextView) findViewById(R.id.text);

        t = new Thread(new CommunicationThread());
        t.start();
    }

    //Print method used in UI thread
    void Print(String message){
        text2.setText(text2.getText().toString() + message + "\n");
    }

    //Test method, use method SafePrint below if everything works
    //NOT SAFE
    //void SafePrint(String message){
     //   text2.setText(text2.getText().toString() + message + "\n");
    //}


    //Print method DO NOT USE in ui thread
    //Thread safe print method
    void SafePrint(String out_text) {
        text2.post(new Printer(out_text));
    }

    class Printer implements Runnable{
        String out_text;
        Printer(String text){out_text = text;}

        public void run(){
            text2.setText(text2.getText().toString() + out_text + "\n");
        }
    }

    void SendAndRecieveData(){
        List<String> data = client.RecieveData();

        for (int i = 0; i < data.size(); i++) {
            SafePrint(data.get(i));
        }

        client.SendData("Card 7\n");
        client.SendData("Card 8\n");
        client.SendData("_endofdata_\n");
        client.LetServerRead();
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

        public <T> void SendData(T data) {
            SafePrint("Client -> Server");
            try {
                if (output != null){
                    output.print(data);
                }
            }
            catch (Exception e) {
                SafePrint(text2.getText() + (e.getMessage()));
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