package net.machina.fieldgame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.machina.fieldgame.data.Game;
import net.machina.fieldgame.data.GameStatus;
import net.machina.fieldgame.data.Riddle;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Ekran ze szczegółami gry
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener, OnDataReceivedListener {
    /**
     * Etykieta identyfikująca wpisy w dzienniku logcat
     */
    private static final String TAG = "FieldGame/Game";

    /**
     * Klucz wykorzystany do przesłania/odebrania danych o grze z/do innej aktywności
     */
    public static final String KEY_GAME_DATA = "game_data";

    /**
     * Wiadomość wyświetlana po znalezieniu właściwego przedmiotu zagadki
     */
    private final String SUCCESS_MASSAGE = "Brawo udało Ci sie znaleść odpowiedni obiekt";

    /**
     * Wiadomość wyświetlana po znalezieniu innego niż właściwy przedmiotu zagadki
     */
    private final String FAILED_MASSAGE = "Niestedy nie o ten obiekt chodziło. Szukaj dalej.";

    /**
     * Pole pomocnicze przechowujące informację o znalezieniu właściwego przedmiotu zagadki
     */
    private Boolean found_object = false;

    /**
     * Unikalny kod zapytania wysyłany podczas odpytywania aktywności {@link ImageLabelingActivity}
     */
    private static final int IMAGE_LABELING_REQUEST = 2137;

    /**
     * Unikalny kod zapytania wysyłany podczas odpytywania aktywności {@link MapsActivity}
     */
    private static final int CODE_MAP_LOAD = 420;

    /**
     * Lista zagadek pobrana z serwera
     */
    private List<Riddle> riddleList;

    /**
     * Aktualnie przetwarzana zagadka
     */
    private Riddle riddleObject;

    /**
     * Referencja do klasy pośredniczącej w połączeniach z serwerem
     */
    private FieldGameNetworkMiddleman middleman;

    /**
     * Informacje o aktualnej grze
     */
    private Game game;

    /**
     * Informacje o aktualnym stanie gry
     */
    private GameStatus gameStatus;

    /**
     * Referencja do etykiety tekstowej opisującej postęp w grze
     */
    private TextView gameTitleView, gameProgressView, gameProgressView2, gameDescriptionView,
            gameDescriptionView2, gameTimeView, gameTimeView2;

    /**
     * Metoda wywoływana podczas pierwszego rysowania okna
     * @param savedInstanceState Zapisany stan aktywności
     */
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
        middleman.refreshToken(this);
        findViewById(R.id.btnStartMap).setOnClickListener(this);
        findViewById(R.id.btnGameLogout).setOnClickListener(this);
        middleman.getProgressForGame(game.getGameID(), this);
        riddleList = new ArrayList<>();

        gameTitleView = findViewById(R.id.gameTitleView);
        gameProgressView = findViewById(R.id.gameProgressView);
        gameProgressView2 = findViewById(R.id.gameProgressView2);
        gameDescriptionView = findViewById(R.id.gameDescriptionView);
        gameDescriptionView2 = findViewById(R.id.gameDescriptionView2);
        gameTimeView = findViewById(R.id.gameTimeView);
        gameTimeView2 = findViewById(R.id.gameTimeView2);
    }

    /**
     * Metoda wyświetlająca postęp w wybranej grze po pobraniu danych z serwera
     */
    public void displayGameData(){
        gameTitleView.setText(game.getGameTitle());
        gameProgressView2.setText("Twoj postęp w grze ");
        gameDescriptionView2.setText("Treść zagadki");
        if(gameStatus.isFinished){
            gameProgressView.setText(game.getGameRiddleCount() + "/" + game.getGameRiddleCount());
            gameDescriptionView.setText("Gra została ukończona.");
            gameTimeView2.setText("Gra została ukończona");
            gameTimeView.setText(gameStatus.getGameEnd().substring(0, gameStatus.getGameEnd().indexOf(".")));
        }
        else{
            gameProgressView.setText(gameStatus.currentRiddle - 1 + "/" + game.getGameRiddleCount());
            gameDescriptionView.setText(riddleObject.getRiddleDescription());
            gameTimeView2.setText("Gra została rozpoczęta");
            gameTimeView.setText(gameStatus.getGameStart().substring(0, gameStatus.getGameStart().indexOf(".")));
        }

    }

    /**
     * Metoda wywoływana podczas naciśnięcia dowolnego obiektu klasy View, w tym przycisków
     * @param v Widok (obiekt klasy View), który został naciśnięty
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnStartMap:
                Intent intent = new Intent(GameActivity.this, MapsActivity.class);
                intent.putExtra(MapsActivity.KEY_GAME_DATA, game);
                intent.putExtra(MapsActivity.KEY_GAME_STATUS_DATA, gameStatus);
                intent.putExtra(MapsActivity.KEY_RIDDLES_DATA, riddleObject);
                startActivityForResult(intent, CODE_MAP_LOAD);
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
        runOnUiThread(() -> {
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("riddles")) {
                    JSONArray riddles = obj.getJSONArray("riddles");
                    JSONObject riddleObj;
                    for (int i = 0; i < riddles.length(); i++) {
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
                    for (Riddle rid : riddleList) {

                        if (rid.getRiddleNo() == gameStatus.getCurrentRiddle()) {
                            riddleObject = rid;
                            displayGameData();
                        }
                    }
                } else if (obj.has("game_data")) {
                    JSONArray game_data = obj.getJSONArray("game_data");
                    JSONObject gameData = game_data.getJSONObject(0);
                    gameStatus = new GameStatus(
                            gameData.getInt("current_riddle"),
                            gameData.getBoolean("finished"),
                            gameData.getString("time_begin"),
                            gameData.getString("time_end")
                    );
                    middleman.getRiddlesForGame(game.getGameID(), this);

                    isFinished(gameStatus.getFinished(), "Ta gra jest już zakończona");
                }else if(obj.has("access_token")) {
                    Log.d(TAG, "Access token refreshed");
                    // Token Refresh
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Metoda wywoływana po otrzymaniu odpowiedzi od innej aktywności (innego "okna")
     * @param requestCode Unikalny kod zapytania
     * @param resultCode Stan odpowiedzi (OK/Anulowano)
     * @param data Dane zwrócone przez aktywność
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_LABELING_REQUEST) {
            if(resultCode == RESULT_OK) {
                if(data != null){
                    ArrayList<String> labelsList = data.getStringArrayListExtra(ImageLabelingActivity.KEY_DATA);
                    for(String labels: labelsList){
                        if(labels.contains(riddleObject.getRiddleDominantObject())) {
                            middleman.advanceGame(game.getGameID(), this);
                            new AlertDialog.Builder(this).setMessage(SUCCESS_MASSAGE).setPositiveButton("OK", null).show();
                        }
                    }
                    if(!found_object)
                        new AlertDialog.Builder(this).setMessage(FAILED_MASSAGE).setPositiveButton("OK", null).show();
                    else
                        found_object = false;
                }
            }
        }
        else if(requestCode == CODE_MAP_LOAD){
            if(resultCode == RESULT_OK) {
                if(data != null){
                    boolean finished = data.getBooleanExtra("finished", false);
                    isFinished(finished, "Gra została zakończona");
                }
            }
        }
    }

    /**
     * Metoda pomocnicza sprawdzająca, czy gra została ukończona
     * @param finished Informacja z serwera, czy gracz ukończył grę
     * @param message Wiadomość, która zostanie wyświetlona
     */
    public void isFinished(Boolean finished, String message) {
        if (finished) {
            new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK", (dialog, which) ->{finish();}).show();
        }
    }
}
