package wesupply.androidclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Socket socket;
    private int portadress = 6000;
    private String ipadress = "192.168.1.";

    private Button searchButtonid;
    private EditText nicknameTxtid, ipadressTxtid, portTxtid;
    //private TextView nicknameResultid, ipadressResultid, portResultid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new Thread(new ClientThread()).start();

        init();
    }

    private void init() {
        searchButtonid = (Button)findViewById(R.id.searchButtonid);
        nicknameTxtid = (EditText)findViewById(R.id.nicknameTxtid);
        ipadressTxtid = (EditText)findViewById(R.id.ipadressTxtid);
        portTxtid = (EditText)findViewById(R.id.portTxtid);

        searchButtonid.setOnClickListener(this);
        //nicknameResultid = (TextView)findViewById(R.id.nicknameResultid);
        //ipadressResultid = (TextView)findViewById(R.id.ipadressResultid);
        //portResultid = (TextView)findViewById(R.id.portResultid);
    }

    @Override
    public void onClick(View view) {
        String nickname = nicknameTxtid.getText().toString();
        ipadress = ipadressTxtid.getText().toString();
        portadress = Integer.parseInt(portTxtid.getText().toString());
        //nicknameResultid.setText(String.valueOf(nickname));
        //ipadressResultid.setText(String.valueOf(ipadress));
        //portResultid.setText(String.valueOf(portadress));
        Intent intent = new Intent(this, Poker_Activity.class);
        intent.putExtra(Poker_Activity.poker_nickname, nickname);
        intent.putExtra(Poker_Activity.ip_adress, ipadress);
        intent.putExtra(Poker_Activity.port_adress, portadress);
        startActivity(intent);

    }
    /*class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(ipadress);

                socket = new Socket(serverAddr, portadress);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }*/
}

