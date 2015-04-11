package com.example.nulp.pebblestuff;

import android.app.Activity;
import android.content.Context;
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

public class MainActivity extends Activity {

    private PebbleKit.PebbleDataReceiver dataReceiver;
    private int appData[] = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PebbleKit.startAppOnPebble(getApplicationContext(), PEBBLEPOINTER_UUID);
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
        dataReceiver = new PebbleKit.PebbleDataReceiver(Constants.GOLF_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                appData[0] = data.getInteger(0).intValue();
                appData[1] = data.getInteger(1).intValue();
                appData[2] = data.getInteger(2).intValue();
                Log.i("something", "Recieved data");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // All data received from the Pebble must be ACK'd, otherwise you'll hit time-outs in the
                        // watch-app which will cause the watch to feel "laggy" during periods of frequent
                        // communication.
                        PebbleKit.sendAckToPebble(context, transactionId);

                    }
                });
            }
        };
        PebbleKit.registerReceivedDataHandler(this, dataReceiver);
    }

    // Update the Activity with the data for a given hole
    public void updateUi() {

        EditText xbox = (EditText) findViewById(R.id.x);
        xbox.setText(Integer.toString(appData[0]));

        EditText ybox = (EditText) findViewById(R.id.y);
        ybox.setText(Integer.toString(appData[1]));

        EditText zbox = (EditText) findViewById(R.id.z);
        zbox.setText(Integer.toString(appData[2]));
        Log.i("something","in update");

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
}
