package com.example.nulp.pebblestuff;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends Activity {

    private final IntentFilter intentFilter =  new IntentFilter();;
    private PebbleKit.PebbleDataReceiver dataReceiver;
    private int appData[] = new int[3];
    private PicFinally mypic;
    public WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    public BroadcastReceiver broadcastreceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager.initialize(getApplicationContext(),getMainLooper(),null);
        //////////////////////////p2p stuff
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //////////////////////////p2p stuff

        mypic = new PicFinally(getApplicationContext());
        setContentView(mypic);
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        Log.i(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));

        PebbleKit.startAppOnPebble(getApplicationContext(), UUID.fromString("273761EB-97DC-4F08-B353-3384A2170902"));
        Log.d("app","got here");

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //make socket
                listenConnection();
            }

            @Override
            public void onFailure(int reasonCode) {

            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();

        // Always deregister any Activity-scoped BroadcastReceivers when the Activity is paused
        if (dataReceiver != null) {
            unregisterReceiver(dataReceiver);
            dataReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // In order to interact with the UI thread from a broadcast receiver, we need to perform any updates through
        // an Android handler. For more information, see: http://developer.android.com/reference/android/os/Handler.html
        final Handler handler = new Handler();

        // To receive data back from the sports watch-app, android
        // applications must register a "DataReceiver" to operate on the
        // dictionaries received from the watch.
        //
        // In this example, we're registering a receiver to listen for
        // button presses sent from the watch, allowing us to page
        // through the holes displayed on the phone and watch.

        dataReceiver = new PebbleKit.PebbleDataReceiver(UUID.fromString("273761eb-97dc-4f08-b353-3384a2170902")) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // All data received from the Pebble must be ACK'd, otherwise you'll hit time-outs in the
                        // watch-app which will cause the watch to feel "laggy" during periods of frequent
                        // communication.
                        appData[0] = data.getInteger(1).intValue();
                        appData[1] = data.getInteger(2).intValue();
                        appData[2] = data.getInteger(3).intValue();

                        double distance = 0.5 * (double) (appData[0]) * (0.2 * 0.2);
                        Log.d("Printing?", Double.toString(distance));

                        Log.i("something", "Recieved data");
                        PebbleKit.sendAckToPebble(context, transactionId);

                        Log.i(getLocalClassName(), "In thread");

                    }
                });

            }
        };
        PebbleKit.registerReceivedDataHandler(this, dataReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinsp {ection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }


    public int tryConnection(String host,int port) {
        Socket socket = new Socket();
        try {

            byte buf[] = new byte[1024];
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, 8888)), 500);
            Log.d("Client", "tries to connect");

        }
        catch(Exception e) {
            return -1;
        }
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return 0;
    }


    public void listenConnection() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();
            InputStream inputstream = client.getInputStream();
            Log.d("Client message: ", inputstream.toString());
            serverSocket.close();
        }
        catch(Exception e) {

        }

    }


}





