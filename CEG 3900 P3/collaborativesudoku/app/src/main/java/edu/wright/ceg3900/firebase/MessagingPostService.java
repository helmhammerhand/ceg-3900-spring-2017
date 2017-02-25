package edu.wright.ceg3900.firebase;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by frodo on 2/16/17.
 */

public class MessagingPostService extends IntentService{

    public MessagingPostService()
    {
        super("MessagingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String request = intent.getStringExtra("c");

        Log.d("Send request", request);

        String query = "{\n" +
                "  \"data\": {\n" +
                "    \"message\": \"" + request + "\"\n" +
                "   },\n" +
                "  \"to\" : \"/topics/sudoku\"\n" +
                "}";

        URL url;
        try {
            url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
            urlCon.setRequestMethod("POST");

            urlCon.setRequestProperty("Content-length", String.valueOf(query.length()));
            urlCon.setRequestProperty("Content-Type", "application/json");
            urlCon.setRequestProperty("Authorization", "key=AAAAG_l15EI:APA91bE1dml0OODt-_40O93JL6" +
                    "dbICebkbfLwz9KrTmNeLK7vG-1UOxsgMl3YFSgJ46BUQFOXvmbwq0tjnT8vAHl69W9arE-GdMVfOpQ" +
                    "wM7p34wWa9IVsdIhEbiXjm6N-xQaA0AYGsXF");

            urlCon.setDoOutput(true); // to be able to write.
            urlCon.setDoInput(true); // to be able to read.
/*
            ObjectOutputStream out = new ObjectOutputStream(urlCon.getOutputStream());
            out.writeObject(request);
            out.close();

            ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
            Object response =  ois.readObject();
            Log.d ("MessagingPostService","Response: "+response.toString());
            ois.close();
            */
            DataOutputStream output = new DataOutputStream(urlCon.getOutputStream());


            output.writeBytes(query);

            output.close();

            DataInputStream input = new DataInputStream(urlCon.getInputStream());


            for (int c = input.read(); c != -1; c = input.read())
                System.out.print((char) c);
            input.close();

            System.out.println("Resp Code:" + urlCon.getResponseCode());
            System.out.println("Resp Message:" + urlCon.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/** Read the object from Base64 string. */
        public static Object fromString( String s ) throws IOException ,
                ClassNotFoundException {
            byte [] data = Base64.decode( s, Base64.DEFAULT );
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(  data ) );
            Object o  = ois.readObject();
            ois.close();
            return o;
        }

        /** Write the object to a Base64 string. */
        public static String toString( Serializable o ) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( o );
            oos.close();
            return Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);
        }
}
