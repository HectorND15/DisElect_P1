package com.example.sms_gps;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TextView tvLatitude1, tvLongitude1, tvIpNum, tvPorNum, tvTime, tvDate;
    private LocationManager locationManager1;
    private Spinner spinner;
    private ToggleButton enviar1;



    //LocationIP.TCP myThreadTcp;
    UDP udpMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.ip_fragment, container, false);


        tvLatitude1 = root.findViewById(R.id.tvLatitude);
        tvLongitude1 = root.findViewById(R.id.tvLongitude);
        tvIpNum = root.findViewById(R.id.ipValue);
        tvPorNum = root.findViewById(R.id.portValue);
        tvTime = root.findViewById(R.id.tvTime);
        tvDate = root.findViewById(R.id.tvDate);

        UDPSender udpSender = new UDPSender();
        Thread hiloUDP = new Thread(udpSender, "The Thread");

        enviar1 = root.findViewById(R.id.toggleButton);
        enviar1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)  {
                Log.d("myTag", String.valueOf(hiloUDP.getState()));
                if(!b){
                    try{
                        udpSender.requestStop();
                    } catch (Exception ignore){

                    }
                    Log.d("myTag","request stop UDP");
                    }
                else {
                    Thread hiloUDP1 = new Thread(udpSender, "The Thread");
                    udpSender.requestStart();
                    hiloUDP1.start();
                }
            }
        });

        spinner = root.findViewById(R.id.spinner1);
        String [] webServers = {
                "Isabella",
                "Hector"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(root.getContext(), R.layout.spinner_item,webServers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerValue = adapterView.getItemAtPosition(i).toString();
                if (spinnerValue == "Isabella"){
                    tvIpNum.setText("192.168.20.102");
                    tvPorNum.setText("23565");
                }else if (spinnerValue=="Hector"){
                    tvIpNum.setText("10.20.42.157");
                    tvPorNum.setText("44444");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationManager1 = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        GetLatLon();
        return root;
    }


    private class UDPSender implements Runnable {
        private boolean stopRequested = false;

        public synchronized void requestStop() {
            this.stopRequested = true;
        }

        public synchronized void requestStart() {
            this.stopRequested = false;
        }

        public synchronized boolean isStopRequested() {
            return this.stopRequested;
        }

        private void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            Log.d("myTag","enviando paquetes UDP");
            while (!isStopRequested()) {
                sleep((3000));
                String host = tvIpNum.getText().toString().trim();
                String portString = tvPorNum.getText().toString().trim();
                String msg = "lat:\n" + tvLatitude1.getText().toString() + "\n" + tvLongitude1.getText().toString() +
                        "\n"+ tvTime.getText().toString() + "\n" + tvDate.getText().toString();
                int port = Integer.parseInt(portString);

                udpMsg = new UDP(host, port);
                try {
                    udpMsg.execute(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;

                }

            }
            Log.d("myTag", "Deteniendo envío");

        }
    }


    public void onResume() {
        super.onResume();
        GetLatLon();
    }

    public void GetLatLon() {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                Date dateTime = new Date(location.getTime());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                tvDate.setText(""+dateFormat.format(dateTime));
                tvTime.setText(""+timeFormat.format(dateTime));
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
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager1.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

}