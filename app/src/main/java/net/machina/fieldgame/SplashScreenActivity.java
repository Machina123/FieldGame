package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

public class SplashScreenActivity extends AppCompatActivity {
    public static final int SPLASH_DISMISS_TIME_MS = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            Intent startIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            SplashScreenActivity.this.startActivity(startIntent);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISMISS_TIME_MS);
        FieldGameNetworkMiddleman.getInstance().login("zaq1", "@WSX", new OnDataReceivedListener() {
            @Override
            public void onDataReceived(String result) {

            }
        });
    }
}
