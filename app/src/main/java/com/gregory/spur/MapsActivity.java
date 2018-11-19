package com.gregory.spur;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gregory.spur.domain.Event;
import com.gregory.spur.services.EventService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = "SPURDebug";
    private static final int REQUEST_CODE_EVENT_CHANGE = 0;
    private static final String EXTRA_USER_ID = "user_id";

    private GoogleMap mMap;
    private LocationManager locationManager;
    private EventService mEventService;
    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private String mUserId;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mUserId = getIntent().getStringExtra(EXTRA_USER_ID);
        mEventService = EventService.getInstance();

        button = (Button) findViewById(R.id.Buttonlogout);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();

               if( FirebaseAuth.getInstance().getCurrentUser()==null){
                   Toast.makeText(getApplicationContext(), "User Logged Out " , Toast.LENGTH_LONG).show();
                   finish();
                }
                else{
                   Toast.makeText(getApplicationContext(), "Logout Failed! " , Toast.LENGTH_LONG).show();
               }


            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // if the network provider is available
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        // Display the logged in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getApplicationContext(), "Logged in user: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
    }

    public static Intent newIntent(Context packageContext, String userId){
        Intent intent = new Intent(packageContext, MapsActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat= location.getLatitude();
        double lon= location.getLongitude();
        LatLng latlng = new LatLng(lat,lon);

        refreshEvents();

        // Center map on your location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14.8f));
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        String eventId = (String) marker.getTag();

        // Check if this is an event marker with an Id
        if (eventId != null) {
            // Launch the update event activity for this event
            Intent intent = ViewEventActivity.newIntent(MapsActivity.this, eventId, mUserId);
            startActivityForResult(intent, REQUEST_CODE_EVENT_CHANGE);
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_CODE_EVENT_CHANGE){
            if(data == null){
                return;
            }

            if(data.getBooleanExtra("event_deleted", false)){
                // If an event was deleted, clear the map of markers and refresh
                mMarkers.clear();
                mMap.clear();
                refreshEvents();
            }

            if(data.getBooleanExtra("event_created", false)){
                refreshEvents();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() has been called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() has been called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() has been called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() has been called");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() has been called");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() has been called");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        refreshEvents();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = CreateEventActivity.newIntent(getApplicationContext(),
                latLng.latitude,
                latLng.longitude,
                null,
                mUserId);
        startActivityForResult(intent, REQUEST_CODE_EVENT_CHANGE);
    }

    private void refreshEvents(){
        mEventService.getEvents(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && mMap != null){
                    Log.d(TAG, "GetEvents returned");
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Event event = document.toObject(Event.class);
                        double lat = event.getLoc().getLatitude();
                        double lng = event.getLoc().getLongitude();
                        String title = event.getName();
                        Marker marker = mMap.addMarker(
                                new MarkerOptions()
                                        .position(new LatLng(lat, lng))
                                        .title(title));
                        marker.setTag(document.getId());
                        mMarkers.add(marker);
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
