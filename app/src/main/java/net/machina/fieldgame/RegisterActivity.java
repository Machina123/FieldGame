package net.machina.fieldgame;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Ekran rejestracji
 */
public class RegisterActivity extends AppCompatActivity implements OnDataReceivedListener {

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
        setContentView(R.layout.activity_register);
        middleman = FieldGameNetworkMiddleman.getInstance();
        txtLogin = findViewById(R.id.txtLogin);
        txtPassword = findViewById(R.id.txtPassword);
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            disableLoginFields();
            middleman.register(txtLogin.getText().toString(), txtPassword.getText().toString(), RegisterActivity.this);
        });
    }

    /**
     * Metoda wywoływana po otrzymaniu odpowiedzi od serwera
     * @param result Odpowiedź serwera w formie tekstowej
     */
    @Override
    public void onDataReceived(String result) {
        runOnUiThread(() -> {
            enableLoginFields();
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("message")) {
                    if(obj.getString("message").toLowerCase().contains("exists")) {
                        Toast.makeText(RegisterActivity.this, "Taki użytkownik już istnieje", Toast.LENGTH_LONG).show();
                    } else if(obj.getString("message").toLowerCase().contains("wrong")) {
                        Toast.makeText(RegisterActivity.this, "Wystąpił błąd serwera. Spróbuj ponownie później", Toast.LENGTH_LONG).show();
                    } else if(obj.getString("message").toLowerCase().contains("created")) {
                        Toast.makeText(RegisterActivity.this, "Zarejestrowano pomyślnie. Możesz się zalogować", Toast.LENGTH_LONG).show();
                        RegisterActivity.this.finish();
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(RegisterActivity.this, "Wystąpił błąd: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
            findViewById(R.id.btnRegister).setEnabled(true);
            findViewById(R.id.txtLogin).setEnabled(true);
            findViewById(R.id.txtPassword).setEnabled(true);
        });
    }
}
