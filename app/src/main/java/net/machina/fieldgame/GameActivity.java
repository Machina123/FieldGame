package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {
    private FieldGameNetworkMiddleman middleman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        middleman = FieldGameNetworkMiddleman.getInstance();
        findViewById(R.id.btnStartQR).setOnClickListener(this);
        findViewById(R.id.btnStartMap).setOnClickListener(this);
        findViewById(R.id.btnStartLabeler).setOnClickListener(this);
        findViewById(R.id.btnGameLogout).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(GameActivity.this)
                .setTitle("Uwaga")
                .setMessage("Na pewno chcesz się wylogować?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    middleman.logout(this);
                })
                .setNegativeButton("Nie", null)
                .show();
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
                middleman.logout(this);
                break;
            default:
                Toast.makeText(GameActivity.this, "Otrzymano onClick od nieznanego widoku", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataReceived(String result) {
        runOnUiThread(() -> {
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("message")) {
                    if(obj.getString("message").contains("revoked")) {
                        Toast.makeText(GameActivity.this, "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
                        GameActivity.this.finish();
                    } else {
                        Toast.makeText(GameActivity.this, "Wystąpił błąd: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GameActivity.this, "Nieprawidłowa odpowiedź serwera", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(GameActivity.this, "Wystąpił błąd: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }
}
