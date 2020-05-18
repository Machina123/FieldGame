package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ekran logowania
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {

    /**
     * Referencja do klasy pośredniczącej w połączeniach z serwerem
     */
    private FieldGameNetworkMiddleman middleman;

    /**
     * Referencja do pola tekstowego zawierającego nazwę użytkownika
     */
    private EditText txtLogin;

    /**
     * Referencja do pola tekstowego zawierającego hasło
     */
    private EditText txtPassword;

    /**
     * Metoda wywoływana przy pierwszym rysowaniu okna
     * @param savedInstanceState Zapisany stan aktywności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        middleman = FieldGameNetworkMiddleman.getInstance();
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
    }

    /**
     * Metoda wywoływana podczas naciśnięcia dowolnego obiektu klasy View, w tym przycisków
     * @param v Widok (obiekt klasy View), który został naciśnięty
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                disableLoginFields();
                middleman.login(txtLogin.getText().toString(), txtPassword.getText().toString(), this);
                break;
            case R.id.btnRegister:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
        }
    }

    /**
     * Metoda wywoływana po otrzymaniu odpowiedzi od serwera
     * @param result Odpowiedź serwera w formie tekstowej
     */
    @Override
    public void onDataReceived(String result) {
        enableLoginFields();
        runOnUiThread(() -> {
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("message")) {
                    if(obj.getString("message").toLowerCase().contains("logged in")) { // Lggged in as...
                        Toast.makeText(MainActivity.this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, GameListActivity.class));
                    } else if(obj.getString("message").toLowerCase().contains("credentials")) { // Wrong credentials
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Błąd")
                                .setMessage("Nieprawidłowe dane logowania. Spróbuj ponownie")
                                .setNeutralButton("OK", null)
                                .show();
                    } else if(obj.getString("message").toLowerCase().contains("exist")) { // User ... doesn't exist
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Błąd")
                                .setMessage("Taki użytkownik nie istnieje")
                                .setNeutralButton("OK", null)
                                .show();
                    } else if(obj.getString("message").toLowerCase().contains("something")) { // Something went wrong
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Błąd")
                                .setMessage("Wystąpił błąd serwera. Skontaktuj się z twórcą aplikacji.")
                                .setNeutralButton("OK", null)
                                .show();
                    }
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Błąd")
                            .setMessage("Wystąpił nieobsługiwany błąd: " + obj.toString())
                            .setNeutralButton("OK", null)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Metoda pomocnicza - blokuje możliwość wprowadzania danych w pola tekstowe
     */
    public void disableLoginFields() {
        runOnUiThread(() ->  {
            findViewById(R.id.layout_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.btnLogin).setEnabled(false);
            findViewById(R.id.btnRegister).setEnabled(false);
            findViewById(R.id.txtLogin).setEnabled(false);
            findViewById(R.id.txtPassword).setEnabled(false);
        });
    }

    /**
     * Metoda pomocnicza - odblokowuje możliwość wprowadzania danych w pola tekstowe
     */
    public void enableLoginFields() {
        runOnUiThread(() -> {
            findViewById(R.id.layout_progress).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnLogin).setEnabled(true);
            findViewById(R.id.btnRegister).setEnabled(true);
            findViewById(R.id.txtLogin).setEnabled(true);
            findViewById(R.id.txtPassword).setEnabled(true);
        });
    }
}
