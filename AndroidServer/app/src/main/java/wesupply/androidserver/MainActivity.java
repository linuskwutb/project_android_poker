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
    static int Cards[] =  {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,
            33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52};
    private int TableCard1;
    private int TableCard2;
    private int TableCard3;
    private int TableCard4;
    private int TableCard5;

    public static void ShuffleDeck(){

        for (int i = 0; i < 10000; ++i){
            int k = (int) (Math.random()*52);
            int l = (int) (Math.random()*52);
            int temp = Cards[k];
            Cards[k] = Cards[l];
            Cards[l] = temp;
        }
    }

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
            ShuffleDeck();
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
            int card1 = Cards[0];
            int card2 = Cards[1];

            server.SendData(card1 + "\n");
            server.SendData(card2 + "\n");
            server.SendData("_endofdata_\n");
            server.LetClientRead();

            List<String> data = server.RecieveData();

            for (int i = 0; i < data.size(); i++) {
                //Print(data.get(i));
                SafePrint(text.getText().toString() + data.get(i) + "\n");
            }
        }

        public void PrintCard(int card) {
            int value_card = card % 13;

            if (card > 0 && card < 14) {
                SafePrint("Hjärter " + value_card);
            }
            if (card > 13 && card < 27) {
                SafePrint("Spader " + value_card);
            }
            if (card > 26 && card < 40) {
                SafePrint("Klöver " + value_card);
            }
            if (card > 39 && card < 53) {
                SafePrint("Ruter " + value_card);
            }
        }

        public void TableCards(){
            TableCard1 = Cards[5];
            TableCard2 = Cards[6];
            TableCard3 = Cards[7];

            PrintCard(TableCard1);
            PrintCard(TableCard2);
            PrintCard(TableCard3);
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

                    TableCards();

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