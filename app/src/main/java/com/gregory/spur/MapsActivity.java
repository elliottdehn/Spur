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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);
                    ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

            return;
        }

        getPermissionToReadLocation();
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
        boolean internet = internet_connection();
        if(internet){
            // Retrieve the data from the marker.
            String eventId = (String) marker.getTag();

            // Check if this is an event marker with an Id
            if (eventId != null) {
                // Launch the update event activity for this event
                Intent intent = ViewEventActivity.newIntent(MapsActivity.this, eventId, mUserId);
                startActivityForResult(intent, REQUEST_CODE_EVENT_CHANGE);
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
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
        if(internet_connection()) {
            refreshEvents();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
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
        boolean internet = internet_connection();
        if(internet) {
            Intent intent = CreateEventActivity.newIntent(getApplicationContext(),
                    latLng.latitude,
                    latLng.longitude,
                    null,
                    mUserId);
            startActivityForResult(intent, REQUEST_CODE_EVENT_CHANGE);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
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

    // Identifier for the permission request
    private static final int READ_FINE_LOC = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    READ_FINE_LOC);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_FINE_LOC) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

            } else {
                Toast.makeText(this, "Read Location Perm Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
