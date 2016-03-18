package wesupply.project_androidpoker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class PokerServerActivity extends AppCompatActivity {

    public static final int SERVERPORT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_poker);

        Server server = new Server(5000);

        if (server.ConnectClient(20000)) { //vänta maximalt 20 sekunder (20000ms) på klient att ansluta
            server.SendData("Card 1\n");
            server.SendData("Card 2\n");
            server.SendData("_endofdata_\n");
            server.LetClientRead();

            List<String> data = server.RecieveData();

            for (int i = 0; i < data.size(); i++) {
                System.out.println(data.get(i));
            }
        }

        server.Close();


        Client client = new Client("127.0.0.1", 5000);

        if (client.Connect() == true) {
            List<String> data = client.RecieveData();

            for (int i = 0; i < data.size(); i++) {
                System.out.println(data.get(i));
            }

            client.SendData("Card 7\n");
            client.SendData("Card 8\n");
            client.SendData("_endofdata_\n");
            client.LetServerRead();
        }

        client.Disconnect();
    }


}
