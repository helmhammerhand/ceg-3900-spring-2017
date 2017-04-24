package com.example.frodo.passwordsecure;

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

public class QueryPassSecurityService extends IntentService{

    public static final String PASSWORD_CHECK_FINISHED = "com.example.frodo.PASSWORD_CHECK_FINISHED";
    public static final String PASSWORD_CRACK_FINISHED = "com.example.frodo.PASSWORD_CRACK_FINISHED";
    public static final String TYPE_WORD_LIST_CHECK = "check";
    public static final String TYPE_CRACK = "crack";

    private static final int PORT_NUMBER = 8080;
    private static final String HOST = "130.108.216.206";//"ec2-35-166-54-53.us-west-2.compute.amazonaws.com";

    public QueryPassSecurityService()
    {
        super("PassCheckService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        String type = intent.getStringExtra("type");

        String password = intent.getStringExtra("password");
        String output = "";
        Log.d("PassSecurityService","Recieved password "+password);

        output = queryCommonWords(HOST, PORT_NUMBER, password);

        // notify activity
        Intent notify = new Intent();
        notify.putExtra("output",output);
        if(type.equals(TYPE_WORD_LIST_CHECK))
            notify.setAction(PASSWORD_CHECK_FINISHED);
        else notify.setAction(PASSWORD_CRACK_FINISHED);
        sendBroadcast(notify);

    }
    public String queryCommonWords(String hostName, int portNumber, String password)
    {
        try (
                Socket sock = new Socket(hostName, portNumber);
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
        ) {
            System.out.println("Sending Request: \n"+password);
            out.writeObject(password);

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


