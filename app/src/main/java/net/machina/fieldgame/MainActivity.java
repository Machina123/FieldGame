package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int CODE_QR_REQUEST = 2137;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_btnQR).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.main_btnQR:
//                Toast.makeText(MainActivity.this, "This will start QR scanner", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(MainActivity.this, ScannerActivity.class), CODE_QR_REQUEST);
                break;
            default:
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_QR_REQUEST) {
            switch(resultCode) {
                case RESULT_CANCELED:
                    Toast.makeText(this, "Code scanning cancelled by user", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_OK:
                    if(data != null) {
                        Toast.makeText(this, "Successfully read QR Code: " + data.getStringExtra(ScannerActivity.KEY_DATA), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No data returned", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Toast.makeText(this, "Unknown result", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
