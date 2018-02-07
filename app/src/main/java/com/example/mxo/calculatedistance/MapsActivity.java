package com.example.mxo.calculatedistance;

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    private double longitude;
    private double latitude;
    private List<LatLng>  userSelectedLatLngs;
    private float distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        userSelectedLatLngs = new ArrayList<>();

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();

        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("i am here!"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)      // Sets the center of the map to Mountain View
                .zoom(11)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void clearMap(View view) {
        mMap.clear();
        userSelectedLatLngs.clear();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (!(userSelectedLatLngs.size() == 2)) {
            userSelectedLatLngs.add(latLng);
            if (userSelectedLatLngs.size() == 1) {
                mMap.addMarker(new MarkerOptions().position((latLng)).title("A"));
            }else {
                mMap.addMarker(new MarkerOptions().position((latLng)).title("B"));
                LatLng originLatLng  = userSelectedLatLngs.get(0);
                LatLng destinationLatLng  = userSelectedLatLngs.get(1);
                PolylineOptions lineOptions = new PolylineOptions()
                        .add(new LatLng(originLatLng.latitude, originLatLng.longitude))
                        .add(new LatLng(destinationLatLng.latitude, destinationLatLng.longitude));
                lineOptions.color(Color.RED);

                float[] result = new float[1];
                Location.distanceBetween(originLatLng.latitude, originLatLng.longitude, destinationLatLng.latitude, destinationLatLng.longitude, result);
                distance = result[0];
                Marker marker =  mMap.addMarker(new MarkerOptions().position(userSelectedLatLngs.get(0)).title(String.valueOf(distance) + "m"));
//                marker.setVisible(false);
                marker.showInfoWindow();
                mMap.addPolyline(lineOptions);
            }
        }
    }
}
