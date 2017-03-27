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
 * Created by Paul Fuchs on 3/25/17.
 */

public class QueryPassSecurityService extends IntentService{

    public static final String PASSWORD_CHECK_FINISHED = "com.example.frodo.PASSWORD_CHECK_FINISHED";

    private static final int PORT_NUMBER = 8080;
    private static final String HOST = "ec2-35-166-54-53.us-west-2.compute.amazonaws.com";

    public QueryPassSecurityService()
    {
        super("PassCheckService");
    }

	/**
	* Called when an activity starts this service
	* The initiating activity should include an extra of type
	* String with the key "password" which contians the password
	* to be checked.
	* The calling activity should have a broadcast reciever registered
	* that listens for intents with the action QueryPassSecurityService.PASSWORD_CHECK_FINISHED
	* in order to recieve the callback when this method completes.
	*/
    @Override
    protected void onHandleIntent(Intent intent) {

        String password = intent.getStringExtra("password");
        String output = "";
        Log.d("PassSecurityService","Recieved password "+password);

        output = queryPassword(HOST, PORT_NUMBER, password);

        // notify activity upon completion
        Intent notify = new Intent();
        notify.putExtra("output",output);
        notify.setAction(PASSWORD_CHECK_FINISHED);
        sendBroadcast(notify);

    }
	/**
	* Queries a server running on the given hostname and portnumber with the given
	* password. Blocks and returns the server's response.
	*/
    public String queryPassword(String hostName, int portNumber, String password)
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


