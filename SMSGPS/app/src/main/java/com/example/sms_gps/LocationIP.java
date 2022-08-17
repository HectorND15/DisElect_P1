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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LocationIP extends AppCompatActivity {

    TextView tvLatitude1;
    TextView tvLongitude1;
    EditText ipAddress;
    EditText portNumber;
    LocationManager locationManager1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_ip);

        tvLatitude1 = findViewById(R.id.tvLatitude);
        tvLongitude1 = findViewById(R.id.tvLongitude);
        ipAddress = findViewById(R.id.ipAddress);
        portNumber = findViewById(R.id.portNumber);
        locationManager1 = (LocationManager) LocationIP.this.getSystemService(Context.LOCATION_SERVICE);

    }


    public void Switch(View v){
        Intent i = new Intent(this, MainActivity.class); //Intención para cambiar hacia el otro activity
        startActivity(i);
    }

    protected void onResume(){
        super.onResume();
        GetLatLon();

    }

    public void GetLatLon(){
        //Acquire a reference to the system location manager
        //Define a listener that responds to location updates
        LocationListener locationListener= new LocationListener() {
            public void onLocationChanged(Location location) {
                //called when a new location is found by the network location provider
                tvLatitude1.setText(""+location.getLatitude());
                tvLongitude1.setText(""+location.getLongitude());

            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }

        };
        // Register the listener with the location manager to receive location updates
        int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager1.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);

    }


}