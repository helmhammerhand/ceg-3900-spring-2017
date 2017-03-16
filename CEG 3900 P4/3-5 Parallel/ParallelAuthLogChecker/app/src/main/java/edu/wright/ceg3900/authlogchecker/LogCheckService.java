package edu.wright.ceg3900.authlogchecker;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by frodo on 3/10/17.
 */

public class LogCheckService extends IntentService{

    public LogCheckService()
    {
        super("LogCheckService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        String ifnm = intent.getStringExtra("infile");
        String ofnm = intent.getStringExtra("outfile");
        String output = "Error";

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
            Log.d("LogCheckService","Need permission to read external storage");

        if(ifnm == null || ofnm == null || ifnm.isEmpty() || ofnm.isEmpty())
            output = "Error: empty file name";
        else {
            Log.d("LogCheckService", ifnm);


            //Regex will match any lines containing the phrase "Invalid user"
            final String INVALID_USER_REGEX = ".*Invalid\\suser.*";

            Stream<String> lines = readLinesFromFile(ifnm);


            if (lines != null) {

                long start = System.currentTimeMillis();

                //Reads the input file by line, filters using the regex, and reduces
                //to a single string with each string seperated by a new-line character
                String invalidUsers = lines.parallel().filter(str -> str.matches(INVALID_USER_REGEX))
                        .reduce("", (a, b) -> a + "\n" + b) + "\n";

                Log.d("LogCheckService","Time: "+(System.currentTimeMillis() - start));
                //writes the string of invalid users to the output file
                output = invalidUsers;
                boolean success = writeToFile(invalidUsers, ofnm);
                if(!success)
                    output = "Error writing to output file";
            } else {
                Log.d("LogCheckService", "Error reading file " + ifnm);
                output = "Error: Input file not found or not readable";
            }
        }

        //TODO notify activity
        Intent notify = new Intent();
        notify.putExtra("output",output);
        notify.setAction("edu.wright.ceg3900.LOG_CHECK_FINISHED");
        sendBroadcast(notify);

    }


    private Stream<String> readLinesFromFile(String fname)
    {
        FileInputStream is;
        BufferedReader reader;
        File sdcard = Environment.getExternalStorageDirectory();
        final File file = new File(sdcard, fname);

        ArrayList<String> arr = new ArrayList<>();
         try {
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                arr.add(reader.readLine());
                while (true) {
                    String line = reader.readLine();
                    if(line != null)
                        arr.add(line);
                    else
                        break;
                }
                return arr.stream();
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



    }

