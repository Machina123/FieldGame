package net.machina.fieldgame;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import net.machina.fieldgame.maps.MarkersHandler;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_REFRESH_TIME = 1000;
    private final long MIN_REFRESH_DISTANCE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        MarkersHandler p1 = new MarkersHandler(-34, 151, "Sydney");
        p1.CreateNewLatLng();
        p1.DrawSearchingArea(mMap, 100);



        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                MarkersHandler playerPosition = new MarkersHandler(location.getLatitude(), location.getLongitude(), "Twoja pozycja");
                playerPosition.CreateNewLatLng();
                playerPosition.DrawMarker(mMap);

                MarkersHandler p2 = new MarkersHandler(location.getLatitude() - 0.0008, location.getLongitude() - 0.0008, "XD");
                p2.CreateNewLatLng();
                p2.DrawMarker(mMap);

                Location loc = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(p2.getLatLng().latitude);
                location.setLongitude(p2.getLatLng().longitude);

                if(location.distanceTo(loc) <= 100){
                    p2.DrawSearchingArea(mMap, 100);
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
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_REFRESH_TIME, MIN_REFRESH_DISTANCE, locationListener);
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
}