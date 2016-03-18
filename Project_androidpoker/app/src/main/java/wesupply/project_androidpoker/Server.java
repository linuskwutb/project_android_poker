package wesupply.project_androidpoker;

/**
 * Created by T4 on 2016-03-15.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket server_socket;
    private Socket client_socket;
    private int server_port;
    private BufferedReader input;
    private PrintWriter output;



    public Server(int port_number) {
        InitVariables(port_number);
    }

    public void Start() {
        SetUp();
    }

    public boolean ConnectClient(int timeout) {
        boolean client_connected = false;
        if (server_socket != null) {
            client_connected = WaitForClient(timeout);
        }
        return client_connected;
    }

    void InitVariables(int port_number) {
        server_socket = null;
        client_socket = null;
        server_port = port_number;
        input = null;
        output = null;
    }

    private void SetUp() {
        try {
            server_socket = new ServerSocket(server_port);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Port number out of bounds (must be between 0 and 65535)");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean WaitForClient(int timeout) {
        boolean client_connected = false;
        try {
            server_socket.setSoTimeout(timeout);
            System.out.println("Waiting for client... (" + (float)timeout/1000 + " s)");

            client_socket = server_socket.accept();
            //client_socket.setSoTimeout(1000);
            client_connected = true;
            System.out.println("Client connected");

            input = new BufferedReader(
                    new InputStreamReader(client_socket.getInputStream()));

            output = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    client_socket.getOutputStream())), false);

        }
        catch(SocketTimeoutException e) {
            System.out.println("No client connected... closing server...");
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return client_connected;
    }

    public <T> void SendData(T data)
    {
        try {
            System.out.println("Server -> Client");
            output.print(data);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void LetClientRead() {
        output.flush();
    }

    public List<String> RecieveData()
    {
        List<String> data_list = new ArrayList<String>();
        try {
            System.out.println("Server <- Client");

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
            System.out.println(e.getMessage());
        }

        return data_list;
    }

    public void Close(){
        try{
            if (client_socket != null)
                client_socket.close();
            if (server_socket != null)
                server_socket.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
