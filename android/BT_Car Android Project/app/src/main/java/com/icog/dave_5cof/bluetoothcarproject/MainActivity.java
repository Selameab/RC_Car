package com.icog.dave_5cof.bluetoothcarproject;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends Activity {


    Button discoverDevicesButton;

    Button forwardButton;
    Button stopButton;
    Button backButton;
    Button leftButton;
    Button rightButton;
    Button leftLightButton;
    Button rightLightButton;
    Button frontLightButton;

    boolean frontLightOn;


    //  String lastAddedDevice;
  BluetoothAdapter mBluetoothAdapter;
    ArrayAdapter<String> mArrayAdapter;

    ListView listView;

    final int REQUEST_ENABLE_BT = 1;

    ArrayList <BluetoothDevice> foundDevices;

    ConnectThread connectThread;


   static EditText commandEditText;

    static TextView messageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Variable initializations


         frontLightOn = false;
        discoverDevicesButton = (Button) findViewById(R.id.discoverdevicesButton);

        discoverDevicesButton.setOnClickListener(discoverButtonOnClickListener);

        forwardButton = (Button) findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(forwardButtonListener);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(stopButtonListener);


        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(backButtonListener);

        leftButton = (Button) findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(leftButtonListener);

        rightButton = (Button) findViewById(R.id.rightButton);
        rightButton.setOnTouchListener(rightButtonListener);

        leftLightButton = (Button) findViewById(R.id.leftLightButon);
        leftLightButton.setOnClickListener(leftLightButtonListener);

        rightLightButton = (Button) findViewById(R.id.rightLightButton);
        rightLightButton.setOnClickListener(rightLightButtonListener);

        frontLightButton = (Button) findViewById(R.id.frontLightButton);
        frontLightButton.setOnClickListener(frontLightButtonListener);


         mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();

         mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

         listView = (ListView) findViewById(R.id.listView1);

        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(listViewOnClickListener);

        foundDevices = new ArrayList<>();

        messageTextView = (TextView) findViewById(R.id.messageTextView);

        // Create a BroadcastReceiver for ACTION_FOUND
         final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter.add(device.getAddress());
                    mArrayAdapter.add(device.getName());
                    foundDevices.add(device);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        }



    View.OnClickListener forwardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            byte[] message = {100};
            connectThread.connectedThread.write(message);
        }
    };

    View.OnClickListener stopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            byte[] message = {102};
            connectThread.connectedThread.write(message);
        }
    };
    View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            byte[] message = {101};
            connectThread.connectedThread.write(message);
        }
    };

    View.OnTouchListener leftButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                byte[] message = {104};
                connectThread.connectedThread.write(message);
                return true;
            }

            else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                byte[] message = {105};
                connectThread.connectedThread.write(message);
                return true;
            }
            return false;
        }
    };
//    View.OnClickListener leftButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//            if(view.isPressed()) {
//                byte[] message = {104};
//                connectThread.connectedThread.write(message);
//            }
//            else{
//                byte[] message = {105};
//                connectThread.connectedThread.write(message);
//            }
//        }
//    };


    View.OnTouchListener rightButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                byte[] message = {103};
                connectThread.connectedThread.write(message);
            }

            else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                byte[] message = {105};
                connectThread.connectedThread.write(message);
            }
            return false;
        }
    };


    View.OnClickListener leftLightButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    View.OnClickListener rightLightButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    View.OnClickListener frontLightButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(frontLightOn) {
                byte[] message = {107};
                connectThread.connectedThread.write(message);
                frontLightOn = false;

            }
            else
            {
                byte[] message = {106};
                connectThread.connectedThread.write(message);
                frontLightOn = true;
            }

        }
    };

    View.OnClickListener discoverButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
            else{
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

   AdapterView.OnItemClickListener listViewOnClickListener = new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           mBluetoothAdapter.cancelDiscovery();
           String value = (String)adapterView.getItemAtPosition(i);
           BluetoothDevice device = null;
           for(BluetoothDevice d: foundDevices) {
               if(d.getAddress()==value)
               {
                   device = d;
                   break;
               }
           }

           connectThread = new ConnectThread(device, mHandler);
           connectThread.run();
           messageTextView.setText(getString(R.string.Connected));
      //     connectThread.connectedThread.run();

//           BluetoothSocket tmp = null;
//           final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//           try {
//               tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//           } catch (IOException e) {
//               e.printStackTrace();
//           }
//     //      connectThread = new ConnectThread(device);

       }
   };
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

    static final int MESSAGE_READ = 9999; // its only identifier to tell to handler what to do with data you passed through.

    // Handler in DataTransferActivity

    public static  Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == MESSAGE_READ)
            {
                byte[] bytes = (byte[])msg.obj;

                char sonarData[] = new char[4];
                int index = 0;
                while((int)bytes[index]>=48 && (int)bytes[index] <=57)
                {
                    sonarData[index] = (char) bytes[index];
                    index++;
                }

                messageTextView.setText(String.valueOf(sonarData));




            }
        }
    };
}
