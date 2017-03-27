package de.tap.easy_xkcd.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.URL;
import java.util.Scanner;

/**
 * Created by frodo on 3/25/17.
 */


public class TagQueryService extends IntentService {

    public static final String QUERY_COMPLETE = "de.tap.easy_xkcd.TAG_QUERY_COMPLETE";

    public TagQueryService()
    {
        super("TagQueryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String type = intent.getStringExtra("type");
        String tag = intent.getStringExtra("tag");
        String comic = intent.getStringExtra("comic");

        try {

            URL url = new URL("http://ec2-35-166-54-53.us-west-2.compute.amazonaws.com/index.php?type=" + type
                    + "&tag=" + tag + "&comic=" + comic);
            String response = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();

            Log.d("TagQueryService", response);

            //extract interesting portion from html
            //the server seperates its response with curly braces
            response = response.substring(response.indexOf('{') + 1, response.indexOf('}'));
            response = response.replaceAll(",", "\n");

            String desc = "";
            switch (type){
            case "taglist":
                desc = "List of all tags in database";
                break;
            case "tag":
                desc = "Added tag "+tag+" to comic "+comic;
                break;
            case "untag":
                desc = "Removed tag "+tag+" from comic "+comic;
                break;
            case "listtag":
                desc = "Tags associated with comic "+comic;
                break;
            case "searchtag":
                desc = "Comics with tag "+tag;
                break;
            }
            // notify activity
            Intent notify = new Intent();
            notify.putExtra("output", desc+"\n\n"+response);
            notify.putExtra("type",type);
            notify.setAction(QUERY_COMPLETE);
            sendBroadcast(notify);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
