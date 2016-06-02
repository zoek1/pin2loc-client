    package com.example.pin2location.activities;

    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.widget.TextView;

    import com.example.pin2location.R;
    import com.example.pin2location.models.Location;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.CameraPosition;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;

    import java.util.ArrayList;

    public class ShowItemActivity extends AppCompatActivity implements
            OnMapReadyCallback {


        GoogleMap mGoogleMap;
        SupportMapFragment mFragment;
        Location loc;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_item);

            Bundle bundle = getIntent().getExtras();
            loc = (Location) bundle.getParcelable("locations");

            TextView e = (TextView) findViewById(R.id.place_description);
            e.setText(loc.getProperties().name);

            mFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mFragment.getMapAsync(this);

            getSupportActionBar().setTitle(loc.getProperties().name);
        }

        @Override
        public void onMapReady(GoogleMap gMap) {
            mGoogleMap = gMap;
            ArrayList<Float> coordinates = loc.getGeometry().coordinates;
            LatLng latlng = new LatLng(coordinates.get(0), coordinates.get(1));

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(loc.getProperties().name));

            CameraPosition cp = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(13)
                    .bearing(90)
                    .tilt(30)
                    .build();

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        }

    }