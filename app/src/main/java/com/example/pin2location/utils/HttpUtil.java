package com.example.pin2location.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.pin2location.models.Location;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by meliodas on 1/06/16.
 */
public class HttpUtil extends AsyncTask<JSONObject, Void, String> {
    private String sendLocation(JSONObject loc) {
        HttpURLConnection connection = null;
        String result = null;
        try {
            URL url = new URL("https://pin2loc.herokuapp.com/locations/new");
            Log.e("URL", url.toString());
            Log.e("URL", loc.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("content-type","application/json; charset=utf-8");
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream());

            out.write(loc.toString());
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            StringBuilder responseStrBuilder = new StringBuilder();

            while ((decodedString = in.readLine()) != null) {
                Log.e("error", decodedString);
                responseStrBuilder.append(decodedString);
            }
            result = responseStrBuilder.toString();
            in.close();
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        } finally {
            connection.disconnect();
        }

        return result;
    }

    protected String doInBackground(JSONObject... params) {
        sendLocation(params[0]);
        return "";
    }
}
