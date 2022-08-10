 package com.example.sms_gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

 public class MainActivity extends AppCompatActivity {

     Button send;
     TextView tvLatitude;
     TextView tvLongitude;
     EditText editTextPhone;

     private int phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Método OnCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.send); //Instanciar Boton
        tvLatitude = (TextView) findViewById(R.id.tvLatitude); //Instancio el TextView de Latitud
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                //Acquiere a reference to the system Location Manager
                LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                //Define a Listener that reponds to location updates
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        tvLatitude.setText(""+location.getLatitude());
                        tvLongitude.setText(""+location.getLongitude());

                    }

                    public void onStatusChanged(String provider,int status, Bundle extra){

                    }
                    public void onProviderEnable(String provider){

                    }
                    public void onProviderDisable(String provider){

                    }


                };
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            }


        });




        /*-----------PERMISSION BLOCK---------------*/
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION); //Verificar si tenemos permiso de la ubicación

        if (permissionCheck== PackageManager.PERMISSION_DENIED){ //Si no tiene o está negado, preguntar por el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        /*-------------------------------------------*/
    }




 }