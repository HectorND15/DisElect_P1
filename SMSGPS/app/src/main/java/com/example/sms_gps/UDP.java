package com.example.sms_gps;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDP extends AsyncTask<String, Void, Void> {

    private String destIp;
    private int port;
    private DatagramSocket socket;
    private InetAddress serverAddress;

    public UDP(String destIp, int port) {
        this.destIp = destIp;
        this.port = port;
        this.socket = null;
        this.serverAddress = null;
    }

    @Override
    protected Void doInBackground(String... voids) {
        String message = voids[0];
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(this.destIp);
            byte[] msg = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(msg, msg.length, serverAddress, this.port);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
