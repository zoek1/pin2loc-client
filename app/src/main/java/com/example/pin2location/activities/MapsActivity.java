package com.example.pin2location.activities;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pin2location.R;
import com.example.pin2location.utils.HttpUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mFragment.getMapAsync(this);

        checkPermissions();

        buildGoogleApiClient();


        Button b = (Button) findViewById(R.id.send_location);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tx = (TextView) findViewById(R.id.description_place);
                String desc = tx.getText().toString();

                if (desc == "") {
                    return;
                }

                JSONArray arr = new JSONArray();
                JSONObject geo = new JSONObject();
                JSONObject properties = new JSONObject();
                JSONObject loc = new JSONObject();

                try {
                    arr.put(currLocationMarker.getPosition().latitude);
                    arr.put(currLocationMarker.getPosition().longitude);

                    geo.put("type", "Point");
                    geo.put("coordinates", arr);

                    properties.put("name", desc);

                    loc.put("geometry", geo);
                    loc.put("properties", properties);
                    loc.put("type", "Feature");

                    (new HttpUtil()).execute(loc);
                } catch (JSONException e) {

                }

                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Toast.makeText(getApplicationContext(), "Connected API", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap = gMap;
        mGoogleMap.setMyLocationEnabled(true);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (mGoogleMap == null) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Log.e("Get current Location", "location -> " + mLastLocation);

        if (mLastLocation != null) {
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            latLng = new LatLng(55.09, 77.81);
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("La position disitntan");
        currLocationMarker = mGoogleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(14).build();


            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Log.d("new Location ", "New location is " + marker.getPosition());
                    currLocationMarker = marker;
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(marker.getPosition()).zoom(14).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
            });
        Log.e("marker", "Add marker");



        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }

                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                Log.e("locationr", "location" + mGoogleApiClient);

                if (mLastLocation != null) {
                    latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    currLocationMarker.setPosition(latLng);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(14).build();

                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

                return true;
            }});

        Log.e("Location", "location request");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Location Allowed", Toast.LENGTH_SHORT)
                            .show();

                    mFragment.getMapAsync(this);
                    buildGoogleApiClient();
                    onStart();
                } else {
                    Toast.makeText(getApplicationContext(), "location Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private Boolean checkPermissions() {
        String[] perms =  {
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        Boolean status = true;

        final List<String> shouldRequest = new ArrayList<>();
        List<String> needed = new ArrayList<>();

        for(String perm : perms) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, perm) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(perm)) {
                    shouldRequest.add(perm);
                } else {
                    needed.add(perm);
                }
                status = false;
            }
        }


        if (shouldRequest.size() > 0) {
            new AlertDialog.Builder(MapsActivity.this)
                    .setMessage("Necesitas permitir el acceso a la ubicación y almacenamiento")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MapsActivity.this, shouldRequest.toArray(new String[0]), REQUEST_CODE_ASK_PERMISSIONS);
                        }
                    }).setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        if (needed.size() > 0) {
            ActivityCompat.requestPermissions(MapsActivity.this, needed.toArray(new String[0]), REQUEST_CODE_ASK_PERMISSIONS);
        }

        return status;
    }
}