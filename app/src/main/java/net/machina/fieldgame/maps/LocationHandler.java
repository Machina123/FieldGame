package net.machina.fieldgame.maps;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Klasa służąca do konwersji obiektu typy LatLng na obiekt typu Location.
 */
public class LocationHandler {

    /**
     * Obiekt typu LatLng na podstawie którego tworzony jest nowy obiekt typu Location.
     */
    private LatLng latLng;

    /**
     *  Obiekt typu Location wygenerowany na podstawie obiektu typu LatLng.
     */
    private Location location;

    /**
     * Konstruktor klasy, wywoływana jest w nim metoda CreateNewLocation tworząca nowy obiekt Location na podstawie przekazanego latLng.
     * @param latLng latLng na podstawie którego ma zostać utworzona nowa lokacja
     */
    public LocationHandler(LatLng latLng){
        this.latLng = latLng;
        CreateNewLocation();
    }

    /**
     * Tworzy nowy obiekt typu Location na podsiawie obiektu typu LatLng przechowywanego w obiekcie.
     */
    private void CreateNewLocation(){
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
    }

    /**
     * Zwraca obiekt typu Location przechowywany w klasie.
     * @return obiekt typu Location
     */
    public Location getLocation() {
        return location;
    }
}