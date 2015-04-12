package com.example.nulp.pebblestuff;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
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

import java.util.UUID;

public class MainActivity extends Activity {

    private PebbleKit.PebbleDataReceiver dataReceiver;
    private GLSurfaceView mGLView;
    private int appData[] = new int[3];
    private PicFinally mypic;
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mypic = new PicFinally(getApplicationContext());
        setContentView(mypic);
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        Log.i(getLocalClassName(), "Pebble is " + (connected ? "connected" : "not connected"));

        PebbleKit.startAppOnPebble(getApplicationContext(), UUID.fromString("273761EB-97DC-4F08-B353-3384A2170902"));
        Log.d("app","got here");

    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new GLSurfaceView(this);
        setContentView(mGLView);
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

        updateUi();

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
                        //mypic.updatePos((int)distance,0);
                        Log.i("something", "Recieved data");
                        PebbleKit.sendAckToPebble(context, transactionId);

                        Log.i(getLocalClassName(), "In thread");
                        updateUi();

                    }
                });


            }
        };
        PebbleKit.registerReceivedDataHandler(this, dataReceiver);
    }

    // Update the Activity with the data for a given hole
    public void updateUi() {
        /*
        EditText xbox = (EditText) findViewById(R.id.x);
        xbox.setText(Integer.toString(appData[0]));

        EditText ybox = (EditText) findViewById(R.id.y);
        ybox.setText(Integer.toString(appData[1]));

        EditText zbox = (EditText) findViewById(R.id.z);
        zbox.setText(Integer.toString(appData[2]));
        */
        Log.i("something", "in update");

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

        //noinspection SimplifiableIfStatement
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
}
