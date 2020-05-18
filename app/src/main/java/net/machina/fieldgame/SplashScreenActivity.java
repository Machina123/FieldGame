package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Ekran powitalny aplikacji
 */
public class SplashScreenActivity extends AppCompatActivity {

    /**
     * Czas, po którym ekran powitalny zostanie zamknięty
     */
    public static final int SPLASH_DISMISS_TIME_MS = 2000;

    /**
     * Metoda wywoływana podczas pierwszego rysowania okna
     * @param savedInstanceState Zapisany stan aktywności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> {
            Intent startIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            SplashScreenActivity.this.startActivity(startIntent);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISMISS_TIME_MS);
    }
}
