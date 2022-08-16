package com.example.sms_gps;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView tvLatitude;
    TextView tvLongitude;
    EditText phoneNumber;

    LocationManager locationManager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //OnCreate Method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        phoneNumber = findViewById(R.id.phoneNumber);
        requestPermission();
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
    }

    public void pushed(View v){
        requestPermission();
        if (isLocationEnabled(this)){
            getLocation();
            sendSMS(tvLatitude.toString().trim(),tvLongitude.toString().trim());
        }

    }

    public void getLocation() {
        //Acquire a reference to the system location manager
            //Define a listener that responds to location updates
            LocationListener locationListener= new LocationListener() {
                public void onLocationChanged(Location location) {
                    //called when a new location is found by the network location provider
                    tvLatitude.setText(""+location.getLatitude());
                    tvLongitude.setText(""+location.getLongitude());
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                public void onProviderEnabled(String provider) {
                }
                public void onProviderDisabled(String provider) {
                }

            };
            // Register the listener with the location manager to receive location updates
            int permissionCheck= ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void sendSMS(String Latitude, String Longitude) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

            String phNumber = phoneNumber.getText().toString().trim();
            String SMS =
                    "UBICACION\n" +
                            "Latitud:     " + Latitude + "\n" +
                            "Longitud:    " + Longitude + "\n\n" +
                             "https://www.google.com/maps/place/" + Latitude + "," + Longitude;

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phNumber, null, SMS, null, null);
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Ask for permission
            requestPermission();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS}, 1);
        }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{SEND_SMS, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 100);
        }
    }
}