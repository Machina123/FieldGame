package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.data.Game;
import net.machina.fieldgame.data.Riddle;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {
    private static final String TAG = "FieldGame/Game";
    public static final String KEY_GAME_DATA = "game_data";
    private static final String KEY_IMAGE_LABELING_DATA = "labels";
    private static final int IMAGE_LABELING_REQUEST = 2137;
    private List<Riddle> riddleList;
    private Riddle riddleObject;
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
        middleman.getRiddlesForGame(1, this);
        riddleList = new ArrayList<>();
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
                startActivityForResult(new Intent(GameActivity.this, ImageLabelingActivity.class), IMAGE_LABELING_REQUEST);
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
                if(data != null){
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
}
