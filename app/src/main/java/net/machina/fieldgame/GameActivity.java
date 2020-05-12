package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.data.Game;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {
    private static final String TAG = "FieldGame/Game";
    public static final String KEY_GAME_DATA = "game_data";
    private FieldGameNetworkMiddleman middleman;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            game = (Game) extras.getSerializable(KEY_GAME_DATA);
            Log.d(TAG, game.toString());
        } else {
            Toast.makeText(this, "Nieprawidłowe wywołanie", Toast.LENGTH_SHORT).show();
        }

        middleman = FieldGameNetworkMiddleman.getInstance();
        findViewById(R.id.btnStartQR).setOnClickListener(this);
        findViewById(R.id.btnStartMap).setOnClickListener(this);
        findViewById(R.id.btnStartLabeler).setOnClickListener(this);
        findViewById(R.id.btnGameLogout).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnStartQR:
                startActivity(new Intent(GameActivity.this, ScannerActivity.class));
                break;
            case R.id.btnStartMap:
                startActivity(new Intent(GameActivity.this, MapsActivity.class));
                break;
            case R.id.btnStartLabeler:
                startActivity(new Intent(GameActivity.this, ImageLabelingActivity.class));
                break;
            case R.id.btnGameLogout:
                finish();
                break;
            default:
                Toast.makeText(GameActivity.this, "Otrzymano onClick od nieznanego widoku", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataReceived(String result) {
        //TODO: implement network access OR delete this activity
    }
}
