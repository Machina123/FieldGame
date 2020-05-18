package net.machina.fieldgame.maps;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Klasa zajmująca sie obsługa znaczników rysowanych na mapie.
 */
public class MarkersHandler {

    /**
     * Obiekt typu LatLng na którym wykonywane są operacje.
     */
    private LatLng latLng;

    /**
     * Opis znacznika przekazywany w konstruktorze.
     */
    private String title;

    /**
     * Konstruktor klasy, konstruktor tworzy nowy obiekt typu latLng.
     * @param title Opis wyświetlany przy najechaniu na znacznik
     */
    public MarkersHandler(String title){
        this.title = title;
        latLng = new LatLng(-54, 151);
    }

    /**
     * Aktualizuje pozycję znacznika na mapie na podstawie przekazanych w parametrach wartości.
     * @param lat szerokośc geograficzna punktu
     * @param lon długość geograficzna punktu
     */
    public void updatePosition(double lat, double lon){
        this.latLng = new LatLng(lat, lon);
    }

    /**
     * Rysuje znacznik na mapie na podstawie przechowywanego w klasie obiektu typu LatLng.
     * @param map obiekt mapy na której ma byc dodany znacznik
     */
    public void DrawMarker(GoogleMap map){
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Rysuje okrąg wokoł obiektu LatLng przechowywanego w klasie.
     * @param map obiekt mapy na której ma byc narysowany okrąg
     * @param radius promień rysowanego okręgu
     */
    public void DrawSearchingArea(GoogleMap map, int radius){
        CircleOptions circle = new CircleOptions();
        circle.center(latLng);
        circle.radius(radius);
        circle.strokeColor(Color.BLUE);
        circle.strokeWidth(10);
        circle.fillColor(Color.argb(90, 0, 205, 255));
        map.addCircle(circle);
    }

    /**
     * Zwraca przechowywany w klasie obiekt typu LatLng.
     * @return      obiekt typu LatLng
     */
    public LatLng getLatLng(){
        return latLng;
    }
}
