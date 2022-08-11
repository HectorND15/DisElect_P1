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
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

 public class MainActivity extends AppCompatActivity {

     Button send;
     TextView tvLatitude;
     TextView tvLongitude;
     EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Método OnCreate
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.send); //Instanciar Boton
        tvLatitude = (TextView) findViewById(R.id.tvLatitude); //Instancio el TextView de Latitud
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED){
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                }

                //Acquiere a reference to the system Location Manager
                LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

                //Define a Listener that reponds to location updates
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        tvLatitude.setText(""+location.getLatitude());
                        tvLongitude.setText(""+location.getLongitude());
                        sendSMS(location.getLatitude(),location.getLongitude());
                    }

                    public void onStatusChanged(String provider,int status, Bundle extra){                    }
                    public void onProviderEnable(String provider){                    }
                    public void onProviderDisable(String provider){                    }


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
    private void sendSMS(double Latitude, double Longitude){
        String phNumber = phoneNumber.getText().toString().trim();
        if (phNumber == ""){
            Toast.makeText(this, "Please, Input a phone number", Toast.LENGTH_SHORT).show();
        } else{
        }
            String SMS =  "Ubicación:\nLatitud:   "+Latitude+"\nLongitud:   "+Longitude;
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phNumber,null,SMS,null,null);
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }




