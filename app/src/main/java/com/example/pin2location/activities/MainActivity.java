package com.example.pin2location.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pin2location.R;
import com.example.pin2location.adapters.SimpleAdapter;
import com.example.pin2location.models.GProperties;
import com.example.pin2location.models.Geometry;
import com.example.pin2location.models.Location;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        loadData();

        final ListView list = (ListView) findViewById(R.id.locations);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location loc = (Location) list.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ShowItemActivity.class);
                intent.putExtra("locations", loc);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(list);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }

    private void loadData() {
        TaskRequest task = new TaskRequest();
        try {
            task.execute(new URL("https://pin2loc.herokuapp.com/locations"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public static String doSimpleRequest(URL url, String method) throws Exception {
        Log.e("URL", url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String result = null;
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null){
                responseStrBuilder.append(inputStr);
            }

            result =responseStrBuilder.toString();


        } finally {
            urlConnection.disconnect();
        }
        return result;
    }


    private class TaskRequest extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            try {
                Thread.sleep(5000);
                return doSimpleRequest(params[0], "GET");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            ListView listLocations = (ListView) findViewById(R.id.locations);

            if (s != null && !s.isEmpty()) {
                try {
                    JSONArray jsonLocations = new JSONArray(s);
                    List<Location> locs = new ArrayList<>();
                    for(int index = 0, size = jsonLocations.length();
                          index < size;
                          index++) {
                        Log.e("request", s);
                        JSONObject oLocation = jsonLocations.getJSONObject(index);

                        Location loc = new Location();
                        GProperties props = new GProperties();
                        props.name = oLocation.getJSONObject("properties").getString("name");
                        props.id = oLocation.getJSONObject("properties").getString("id");

                        Geometry geo = new Geometry();
                        geo.coordinates = new ArrayList<>();
                        JSONObject geoObject = oLocation.getJSONObject("geometry");
                        JSONArray coor = geoObject.getJSONArray("coordinates");

                        geo.type = geoObject.getString("type");

                        try {
                            geo.coordinates.add(Float.parseFloat(coor.getString(0)));
                            geo.coordinates.add(Float.parseFloat(coor.getString(1)));
                        } catch (Exception e) {

                        }

                        loc.setProperties(props);
                        loc.setGeometry(geo);

                        locs.add(loc);
                    }
                    SimpleAdapter sa = new SimpleAdapter(getApplicationContext(), locs);
                    listLocations.setAdapter(sa);
                    progress.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
