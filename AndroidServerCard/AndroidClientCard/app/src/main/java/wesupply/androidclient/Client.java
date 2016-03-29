/*package wesupply.androidclient;

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

public class Client {
    private Socket socket;
    private String server_ip;
    private int server_port;
    private InetAddress server_address;
    private PrintWriter output;
    private BufferedReader input;
    private boolean connected;

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
        connected = false;
    }

    public boolean Connect() {
        try {
            server_address = InetAddress.getByName(server_ip);
            socket = new Socket(server_address, server_port);
            //socket.setSoTimeout(2000);
            connected = true;

            input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            output = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())), false);
        }
        catch (UnknownHostException e) {
            System.out.println("Server not found, check IP-adress");
        }
        catch (IllegalArgumentException e) {
            System.out.println("Port number out of bounds (must be between 0 and 65535)");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return connected;
    }

    public <T> void SendData(T data) {
        System.out.println("Client -> Server");
        try {
            if (output != null){
                output.print(data);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void LetServerRead() {
        output.flush();
    }

    public List<String> RecieveData() {
        List<String> data_list = new ArrayList<String>();
        try {
            System.out.println("Client <- Server");

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

    public void Disconnect(){
        try {
            if (socket != null)
                socket.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
*/