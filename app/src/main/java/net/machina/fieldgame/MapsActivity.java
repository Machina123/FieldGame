package net.machina.fieldgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.machina.fieldgame.Data.Riddle;
import net.machina.fieldgame.maps.LocationHandler;
import net.machina.fieldgame.maps.MarkersHandler;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, OnDataReceivedListener {

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private FieldGameNetworkMiddleman middleman;
    private Button take_picture_btn;
    List<Riddle> riddleList;
    private Riddle riddleObject;

    private final long MIN_REFRESH_TIME = 1000;
    private final long MIN_REFRESH_DISTANCE = 1;
    private static final int IMAGE_LABELING_REQUEST = 2137;
    private static final String KEY_DATA = "labels";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        middleman = FieldGameNetworkMiddleman.getInstance();
        middleman.getRiddlesForGame(1, this);
        riddleList = new ArrayList<>();
        take_picture_btn = findViewById(R.id.btn_goto_labelactivity);
        take_picture_btn.setOnClickListener(this);
        findViewById(R.id.description_button).setOnClickListener(this);
        take_picture_btn.setVisibility(View.GONE);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        MarkersHandler playerPosition = new MarkersHandler("Twoja lokalizacja");
        MarkersHandler riddle = new MarkersHandler("Riddle");

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                riddle.updatePosition(riddleObject.getRIDDLE_LATITUDE(), riddleObject.getRIDDLE_LONGITUDE());
                riddle.DrawMarker(mMap);
                playerPosition.updatePosition(location.getLatitude(), location.getLongitude());
                playerPosition.DrawMarker(mMap);

                Location riddleLocation = new LocationHandler(riddle.getLatLng()).getLocation();


                if(location.distanceTo(riddleLocation) <= riddleObject.getRIDDLE_RADIUS()){
                    riddle.DrawSearchingArea(mMap, riddleObject.getRIDDLE_RADIUS());
                    take_picture_btn.setVisibility(View.VISIBLE);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_goto_labelactivity:
                startActivity(new Intent(MapsActivity.this, ImageLabelingActivity.class));
                break;
            case R.id.description_button:
                new AlertDialog.Builder(this).setMessage(riddleObject.getRIDDLE_DESCRIPTION()).setPositiveButton("OK", null).show();
        }
    }

    @Override
    public void onDataReceived(String result) {
        runOnUiThread(() ->{
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("riddles")){
                    JSONArray riddles = obj.getJSONArray("riddles");
                    JSONObject riddleObj;
                    for(int i = 0; i < riddles.length(); i++){
                        riddleObj = riddles.getJSONObject(i);
                        Riddle riddle = new Riddle(
                          riddleObj.getInt("riddle_no"),
                          riddleObj.getString("description"),
                          riddleObj.getDouble("latitude"),
                          riddleObj.getDouble("longitude"),
                          riddleObj.getInt("radius"),
                          riddleObj.getString("dominant_object")
                        );
                        riddleList.add(riddle);
                    }
                    for(Riddle rid: riddleList){
                        if(rid.getRIDDLE_NO() == 2){
                            riddleObject = rid;
                        }
                    }
                }
                else{
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_LABELING_REQUEST) {
            if(resultCode == RESULT_OK) {
                    assert data != null;
                    ArrayList<String> labelsList = data.getStringArrayListExtra(ImageLabelingActivity.KEY_DATA);
                    for(String labels: labelsList){
                        if(labels.contains(riddleObject.getRIDDLE_DOMINANT_OBJECT()))
                            Toast.makeText(this, "Zagadka OK", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(this, "Zagadka nie OK", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }
}