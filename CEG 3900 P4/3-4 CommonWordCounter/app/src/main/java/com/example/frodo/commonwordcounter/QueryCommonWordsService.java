package com.example.frodo.commonwordcounter;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by frodo on 3/10/17.
 */

public class QueryCommonWordsService extends IntentService{

    private static final int PORT_NUMBER = 80;
    private static final String URL_FILE = "HI";

    public QueryCommonWordsService()
    {
        super("LogCheckService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        String hostName = intent.getStringExtra("host");
        String urlfname = intent.getStringExtra("urlfile"); //"urls.txt";
        String outfname = intent.getStringExtra("outfile");
        int numWords = intent.getIntExtra("count",0);// 25;
        String output = "";

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("LogCheckService", "Need permission to read external storage");
            output = "Error: missing permission";
        }
        else
        if(urlfname == null || outfname == null || urlfname.isEmpty() || outfname.isEmpty())
            output = "Error: empty file name";
        else {

            String urls = readFileToString(urlfname);

            if(urls == null)
                output = "Error: file not found or not readable";
            else {
                output = queryCommonWords(hostName, PORT_NUMBER, urls, numWords);

                writeToFile(output, outfname);
            }
        }

        //TODO notify activity
        Intent notify = new Intent();
        notify.putExtra("output",output);
        notify.setAction("edu.wright.ceg3900.LOG_CHECK_FINISHED");
        sendBroadcast(notify);

    }
    public String queryCommonWords(String hostName, int portNumber, String urls, int numWords)
    {
        try (
                Socket sock = new Socket(hostName, portNumber);
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
        ) {
            String urlstr = urls;
            urlstr = Integer.toString(numWords) + '\n' + urlstr;

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

    private String readFileToString(String fname)
    {
        FileInputStream is;
        BufferedReader reader;
        File sdcard = Environment.getExternalStorageDirectory();
        final File file = new File(sdcard, fname);


       StringBuilder strb = new StringBuilder();
         try {
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                strb.append(reader.readLine()+"\n");
                while (true) {
                    String line = reader.readLine();
                    if(line != null)
                        strb.append(line + "\n");
                    else
                        break;
                }
                return strb.toString();
            }
            else
            {
                Log.d("LogCheckService","File "+file.getAbsolutePath()+" does not exist");
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private boolean writeToFile(String message, String fname)
    {
        File sdcard = Environment.getExternalStorageDirectory();
        final File file = new File(sdcard, fname);

        try {
            Log.d("LogCheckService", "Writing to "+file.getAbsolutePath());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            outputStreamWriter.write(message);
            outputStreamWriter.close();

            return true;
        }
        catch (IOException e) {
            Log.d("LogCheckService", "File write failed: " + e.toString());
        }
        return false;

    }

    private void hi()
    {

}


}


