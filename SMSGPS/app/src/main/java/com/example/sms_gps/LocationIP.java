package com.example.sms_gps;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


public class LocationIP extends AppCompatActivity {

    private TextView tvLatitude1;
    private TextView tvLongitude1;
    private EditText ipAddress;
    private EditText portNumber;
    private LocationManager locationManager1;
    private RadioButton tcp, udp;
    TCP myThreadTcp;
    UDP udpMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_ip);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tvLatitude1 = findViewById(R.id.tvLatitude);
        tvLongitude1 = findViewById(R.id.tvLongitude);
        ipAddress = findViewById(R.id.ipAddress);
        portNumber = findViewById(R.id.portNumber);
        tcp = findViewById(R.id.tcp_rbtn);
        udp = findViewById(R.id.udp_rbtn);

        myThreadTcp = new TCP();
        new Thread(myThreadTcp).start();
        locationManager1 = (LocationManager) LocationIP.this.getSystemService(Context.LOCATION_SERVICE);

    }

    public void Switch(View v) {
        Intent i = new Intent(this, MainActivity.class); //Intención para cambiar hacia el otro activity
        startActivity(i);
    }

    protected void onResume() {
        super.onResume();
        GetLatLon();

    }

    public void GetLatLon() {
        //Acquire a reference to the system location manager
        //Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //called when a new location is found by the network location provider
                tvLatitude1.setText("" + location.getLatitude());
                tvLongitude1.setText("" + location.getLongitude());

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };
        // Register the listener with the location manager to receive location updates
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager1.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    private class TCP implements Runnable {

        private volatile String msg="";
        Socket socket;
        DataOutputStream dos;
        private volatile int port;
        private volatile String host="";


        @Override
        public void run() {
            try {
                socket = new Socket(host,port);
                dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(msg);
                dos.close();
                dos.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMsgTcp(String msg, String host, int port){
            this.msg = msg;
            this.host = host;
            this.port = port;
            run();
        }
    }

    public void pushed1(View v){
        GetLatLon();
        String host = ipAddress.getText().toString().trim();
        String portString = portNumber.getText().toString().trim();
        if (!host.matches("") || !portString.matches("")) {
            String msg = "lat: " + tvLatitude1.getText().toString() + "\nlon: " + tvLongitude1.getText().toString();
            int port = Integer.parseInt(portString);
            if (tcp.isChecked()) {
                try {
                    myThreadTcp.sendMsgTcp(msg, host, port);
                    Toast.makeText(this, "¡Paquete TCP ENVIADO!", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Error al enviar TCP", Toast.LENGTH_SHORT).show();
                }
            } else if (udp.isChecked()) {
                udpMsg = new UDP(host,port);
                try {
                    udpMsg.execute(msg);
                    Toast.makeText(this, "¡Paquete UDP ENVIADO!", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR al enviar UDP", Toast.LENGTH_SHORT).show();
                }
            }
        } else{
            Toast.makeText(this, "Digite IP y Puerto", Toast.LENGTH_SHORT).show();
        }
    }
}