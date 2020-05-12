package net.machina.fieldgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import net.machina.fieldgame.data.Game;
import net.machina.fieldgame.data.GameStatus;
import net.machina.fieldgame.data.Riddle;
import net.machina.fieldgame.maps.LocationHandler;
import net.machina.fieldgame.maps.MarkersHandler;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, OnDataReceivedListener {

    public static final String KEY_GAME_DATA = "game_data";
    public static final String KEY_GAME_STATUS_DATA = "game_status_data";
    public static final String KEY_RIDDLES_DATA = "riddles_data";
    public static final String KEY_GAME_FINISHED = "finished";
    private static final String TAG = " tak";
    private static final int IMAGE_LABELING_REQUEST = 2137;
    private static final String KEY_DATA = "labels";
    private final String SUCCESS_MASSAGE = "Brawo udało Ci sie znaleść odpowiedni obiekt";
    private final String FAILED_MASSAGE = "Niestedy nie o ten obiekt chodziło. Szukaj dalej.";
    private final long MIN_REFRESH_TIME = 1000;
    private final long MIN_REFRESH_DISTANCE = 1;
    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private FieldGameNetworkMiddleman middleman;
    private Button take_picture_btn;
    private List<Riddle> riddleList;
    private Riddle riddleObject;
    private Game game;
    private GameStatus gameStatus;
    private Boolean found_object = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        middleman = FieldGameNetworkMiddleman.getInstance();
        middleman.refreshToken(this);
        riddleList = new ArrayList<>();
        take_picture_btn = findViewById(R.id.btn_goto_labelactivity);
        take_picture_btn.setOnClickListener(this);
        findViewById(R.id.description_button).setOnClickListener(this);
        take_picture_btn.setVisibility(View.GONE);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            game = (Game) extras.getSerializable(KEY_GAME_DATA);
            gameStatus = (GameStatus) extras.getSerializable(KEY_GAME_STATUS_DATA);
            riddleObject = (Riddle) extras.getSerializable(KEY_RIDDLES_DATA);
            Log.d(TAG, game.toString());
        } else {
            Toast.makeText(this, "Nieprawidłowe wywołanie", Toast.LENGTH_SHORT).show();
        }

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
                new Handler().postDelayed(() -> {
                    riddle.updatePosition(riddleObject.getRIDDLE_LATITUDE(), riddleObject.getRIDDLE_LONGITUDE());
                    riddle.DrawMarker(mMap);
                    playerPosition.updatePosition(location.getLatitude(), location.getLongitude());
                    playerPosition.DrawMarker(mMap);

                    Location riddleLocation = new LocationHandler(riddle.getLatLng()).getLocation();


                    if (location.distanceTo(riddleLocation) <= riddleObject.getRIDDLE_RADIUS()) {
                        riddle.DrawSearchingArea(mMap, riddleObject.getRIDDLE_RADIUS());
                        take_picture_btn.setVisibility(View.VISIBLE);

                    }
                }, 1000);

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
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_REFRESH_TIME, MIN_REFRESH_DISTANCE, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto_labelactivity:
                startActivityForResult(new Intent(MapsActivity.this, ImageLabelingActivity.class), IMAGE_LABELING_REQUEST);
                break;
            case R.id.description_button:
                new AlertDialog.Builder(this).setMessage(riddleObject.getRIDDLE_DESCRIPTION()).setPositiveButton("OK", null).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_LABELING_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    ArrayList<String> labelsList = data.getStringArrayListExtra(ImageLabelingActivity.KEY_DATA);
                    for (String labels : labelsList) {
                        if (labels.contains(riddleObject.getRIDDLE_DOMINANT_OBJECT())) {
                            middleman.advanceGame(game.getGameID(), this);
                            new AlertDialog.Builder(this).setMessage(SUCCESS_MASSAGE).setPositiveButton("OK", null).show();
                            found_object = true;
                        }
                    }
                    if (!found_object)
                        new AlertDialog.Builder(this).setMessage(FAILED_MASSAGE).setPositiveButton("OK", null).show();
                    else
                        found_object = false;
                }
            }
        }
    }

    public void isFinished(Boolean finished, String message) {
        if (finished) {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) ->{
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(KEY_GAME_FINISHED, true);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .show();
        }
    }

    @Override
    public void onDataReceived(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            if (obj.has("game_data")) {
                JSONArray game_data = obj.getJSONArray("game_data");
                JSONObject gameData = game_data.getJSONObject(0);
                gameStatus = new GameStatus(
                        gameData.getInt("current_riddle"),
                        gameData.getBoolean("finished")
                );
                isFinished(gameStatus.getFinished(), "Gra została ukończona");
            } else if (obj.has("access_token")) {
                Log.d(TAG, "Access token refreshed");
                // Token Refresh
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}