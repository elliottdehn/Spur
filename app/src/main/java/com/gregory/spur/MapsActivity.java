package com.gregory.spur;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private LocationManager locationManager;
    private String TAG="SPURDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        //Button button= (Button)findViewById(R.id.button);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat= location.getLatitude();
                    double lon= location.getLongitude();
                    LatLng latlng=new LatLng(lat,lon);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList= geocoder.getFromLocation(lat,lon,1);
                        String str =addressList.get(0).getLocality();
                        str +=addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latlng).title("Marker in "+ str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10.8f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


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
            });

        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat= location.getLatitude();
                    double lon= location.getLongitude();
                    LatLng latlng=new LatLng(lat,lon);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList= geocoder.getFromLocation(lat,lon,1);
                        String str =addressList.get(0).getLocality();
                        str +=addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latlng).title("Marker in "+ str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10.8f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
            });

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
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
