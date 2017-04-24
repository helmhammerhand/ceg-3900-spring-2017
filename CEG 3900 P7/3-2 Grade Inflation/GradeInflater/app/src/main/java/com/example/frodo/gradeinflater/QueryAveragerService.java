package com.example.frodo.gradeinflater;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by frodo on 3/10/17.
 */

public class QueryAveragerService extends IntentService{

    private static final int PORT_NUMBER = 8080;

    public QueryAveragerService()
    {
        super("QueryAveragerService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("LogCheckService", "Recieved intent");

        String driveurl = intent.getStringExtra("driveurl");
         String host = intent.getStringExtra("host");

        String output = "";

        Log.d("LogCheckService", driveurl+" "+host);

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("LogCheckService", "Need permission to access the internet");
            output = "Error: missing permission";
        }
        else
        {
                output = queryAverager(host, PORT_NUMBER, driveurl);
        }

        // notify activity
        Intent notify = new Intent();
        notify.putExtra("output",output);
        notify.setAction("edu.wright.ceg3900.LOG_CHECK_FINISHED");
        sendBroadcast(notify);

    }
    public String queryAverager(String hostName, int portNumber, String driveurl)
    {
        try {
                Socket sock = new Socket(hostName, portNumber);
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());

            String urlstr = driveurl;

            System.out.println("Sending Request: \n"+urlstr);
            out.writeObject(urlstr);

            String response = (String) in.readObject();
            System.out.println("Recieved Response: \n"+response);

            return response;

        } catch(UnknownHostException e)
        {
            e.printStackTrace();
        } catch(IOException e)
        {
            e.printStackTrace();
        } catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return "";
    }


}


