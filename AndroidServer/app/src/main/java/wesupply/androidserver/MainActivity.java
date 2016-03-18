package wesupply.androidserver;


        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.TextView;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.net.SocketTimeoutException;
        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView text;
    private Server server;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);

        server = new Server(5500);
        server.Start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        server.Close();
    }

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
            if (server_socket != null) {
                server.WaitForClient(20000);
            }
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
            } catch (IllegalArgumentException e) {
                Print("Port number out of bounds (must be between 0 and 65535)" + "\n");
            } catch (Exception e) {
                Print(e.getMessage());
            }
        }

        private void WaitForClient(int timeout) {
            try {
                t = new Thread(new CommunicationThread(timeout));
                t.start();
            }
            catch(Exception e){
                Print(e.getMessage());
            }
        }

        // Used in CommunicationThread
        public <T> void SendData(T data)
        {
            try {
                SafePrint("Server -> Client");
                output.print(data);
            }
            catch(Exception e) {
                SafePrint(e.getMessage());
            }
        }

        // Used in CommunicationThread
        public void LetClientRead() {
            output.flush();
        }

        // Used in CommunicationThread
        public List<String> RecieveData()
        {
            List<String> data_list = new ArrayList<String>();
            try {
                SafePrint("Server <- Client");

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

        public void Close(){
            try{
                if (client_socket != null)
                    client_socket.close();
                if (server_socket != null)
                    server_socket.close();
            }
            catch(Exception e){
                Print(e.getMessage());
            }
        }

        //Print method used in UI thread
        void Print(String message){
            text.setText(text.getText().toString() + message + "\n");
        }

        //Test method, use method SafePrint below if everything works
        //NOT SAFE
       // void SafePrint(String message){
       //     text.setText(text.getText().toString() + message + "\n");
       // }


        //Print method DO NOT USE in ui thread
        //Thread safe print method
        void SafePrint(String out_text) {
            text.post(new Printer(out_text));
        }

        class Printer implements Runnable{
            String out_text;
            Printer(String text){out_text = text;}

            public void run(){
                text.setText(text.getText().toString() + out_text + "\n");
            }
        }


        //Used in CommunicationThread
        void SendAndRecieveData(){
            server.SendData("Card 1\n");
            server.SendData("Card 2\n");
            server.SendData("_endofdata_\n");
            server.LetClientRead();

            List<String> data = server.RecieveData();

            for (int i = 0; i < data.size(); i++) {
                //Print(data.get(i));
                SafePrint(text.getText().toString() + data.get(i) + "\n");
            }
        }

        class CommunicationThread implements Runnable{
            int timeout_;
            public CommunicationThread(int timeout) {
                timeout_ = timeout;
            }

            public void run(){
                try {
                    server_socket.setSoTimeout(timeout_);
                    Print("Waiting for client... (" + (float) timeout_ / 1000 + " s)");
                    client_socket = server_socket.accept();

                    SafePrint("Client connected...");

                    input = new BufferedReader(
                            new InputStreamReader(client_socket.getInputStream()));

                    output = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            client_socket.getOutputStream())), false);

                    SendAndRecieveData();
                }
                catch(SocketTimeoutException e) {
                    SafePrint("No client connected... closing server...");
                }
                catch(Exception e) {
                    SafePrint(e.getMessage());
                }
            }
        }
    }
}