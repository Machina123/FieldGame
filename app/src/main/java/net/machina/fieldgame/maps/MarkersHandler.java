package net.machina.fieldgame.maps;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkersHandler {

    private LatLng latLng;
    private String title;

    public MarkersHandler(String title){
        this.title = title;
        latLng = new LatLng(-54, 151);
    }

    public void updatePosition(double lat, double lon){
        this.latLng = new LatLng(lat, lon);
    }


    public void DrawMarker(GoogleMap map){
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void DrawSearchingArea(GoogleMap map, int radius){
        CircleOptions circle = new CircleOptions();
        circle.center(latLng);
        circle.radius(radius);
        circle.strokeColor(Color.BLUE);
        circle.strokeWidth(10);
        circle.fillColor(Color.argb(90, 0, 205, 255));
        map.addCircle(circle);
    }

    public LatLng getLatLng(){
        return latLng;
    }
}
