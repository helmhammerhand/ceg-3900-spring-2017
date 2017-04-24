package com.example.frodo.cloudcat;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by frodo on 3/10/17.
 */

public class QueryHashcatService extends IntentService{

    private static final int PORT_NUMBER = 8080;

    public QueryHashcatService()
    {
        super("QueryHashcatService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("LogCheckService", "Recieved intent");

        String hashurl = intent.getStringExtra("hashurl");
        String wordlisturl = intent.getStringExtra("wordlisturl"); //"urls.txt";
        String host = intent.getStringExtra("host");

        String output = "";

        Log.d("LogCheckService", hashurl+" "+wordlisturl+" "+host);

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("LogCheckService", "Need permission to access the internet");
            output = "Error: missing permission";
        }
        else
        {
                output = queryHashcat(host, PORT_NUMBER, hashurl, wordlisturl);
        }

        // notify activity
        Intent notify = new Intent();
        notify.putExtra("output",output);
        notify.setAction("edu.wright.ceg3900.LOG_CHECK_FINISHED");
        sendBroadcast(notify);

    }
    public String queryHashcat(String hostName, int portNumber, String hashurl, String wordlisturl)
    {
        try {
                Socket sock = new Socket(hostName, portNumber);
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());

            String urlstr = hashurl + '\n' + wordlisturl;

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


