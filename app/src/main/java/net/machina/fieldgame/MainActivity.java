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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {

//    public static final int CODE_QR_REQUEST = 2137;
    private FieldGameNetworkMiddleman middleman;
    private EditText txtLogin, txtPassword;

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

    @Override
    public void onDataReceived(String result) {
        enableLoginFields();
        runOnUiThread(() -> {
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("message")) {
                    if(obj.getString("message").toLowerCase().contains("logged in")) { // Lggged in as...
                        Toast.makeText(MainActivity.this, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, GameActivity.class));
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

    public void disableLoginFields() {
        runOnUiThread(() ->  {
            findViewById(R.id.layout_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.btnLogin).setEnabled(false);
            findViewById(R.id.btnRegister).setEnabled(false);
            findViewById(R.id.txtLogin).setEnabled(false);
            findViewById(R.id.txtPassword).setEnabled(false);
        });
    }

    public void enableLoginFields() {
        runOnUiThread(() -> {
            findViewById(R.id.layout_progress).setVisibility(View.INVISIBLE);
            findViewById(R.id.btnLogin).setEnabled(true);
            findViewById(R.id.btnRegister).setEnabled(true);
            findViewById(R.id.txtLogin).setEnabled(true);
            findViewById(R.id.txtPassword).setEnabled(true);
        });
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CODE_QR_REQUEST) {
//            switch(resultCode) {
//                case RESULT_CANCELED:
//                    Toast.makeText(this, "Code scanning cancelled by user", Toast.LENGTH_SHORT).show();
//                    break;
//                case RESULT_OK:
//                    if(data != null) {
//                        Toast.makeText(this, "Successfully read QR Code: " + data.getStringExtra(ScannerActivity.KEY_DATA), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "No data returned", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                default:
//                    Toast.makeText(this, "Unknown result", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}
