package net.machina.fieldgame.maps;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class LocationHandler {

    private LatLng latLng;
    private Location location;

    public LocationHandler(LatLng latLng){
        this.latLng = latLng;
        CreateNewLocation();
    }

    private void CreateNewLocation(){
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
    }

    public Location getLocation() {
        return location;
    }
}