package net.machina.fieldgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.machina.fieldgame.adapters.GameAdapter;
import net.machina.fieldgame.adapters.GameSelectedListener;
import net.machina.fieldgame.data.Game;
import net.machina.fieldgame.network.APIDetails;
import net.machina.fieldgame.network.FieldGameNetworkMiddleman;
import net.machina.fieldgame.network.OnDataReceivedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Ekran listy gier, do których dołączył użytkownik
 */
public class GameListActivity extends AppCompatActivity implements OnDataReceivedListener, GameSelectedListener, View.OnClickListener {

    /**
     * Unikalny kod zapytania, dołączany w celu odróżnienia odpowiedzi od skanera kodów QR od innych
     */
    private static final int CODE_QR_REQUEST = 2137;

    /**
     * Referencja do widoku listy
     */
    private RecyclerView recyclerView;

    /**
     * Referencja do przycisku umożliwiającego dołączenie do gry
     */
    private FloatingActionButton fabJoin;

    /**
     * Adapter widoku listy gier
     */
    private GameAdapter adapter;

    /**
     * Lista gier pobrana z serwera
     */
    private List<Game> gameList;

    /**
     * Referencja do klasy pośredniczącej w połączeniach z serwerem
     */
    private FieldGameNetworkMiddleman middleman;

    /**
     * Etykieta identyfikująca wpisy w dzienniku logcat
     */
    public static final String TAG = "FieldGame/GameList";

    /**
     * Metoda wywoływana przy pierwszym rysowaniu okna
     * @param savedInstanceState Zapisany stan aktywności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        middleman = FieldGameNetworkMiddleman.getInstance();
        recyclerView = findViewById(R.id.viewList);
        fabJoin = findViewById(R.id.btnJoin);
        fabJoin.setOnClickListener(this);
        gameList = new ArrayList<>();
        adapter = new GameAdapter(gameList);
        adapter.setContext(this);
        adapter.setListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        middleman.refreshToken(this);
        middleman.getMyProgress(this);
    }

    /**
     * Metoda wywoływana po otrzymaniu odpowiedzi od serwera
     * @param result Odpowiedź serwera w formie tekstowej
     */
    @Override
    public void onDataReceived(String result) {
        runOnUiThread(() -> {
            try {
                JSONObject obj = new JSONObject(result);
                if(obj.has("game_data")) {      // moje gry
                    JSONArray gameData = obj.getJSONArray("game_data");
                    JSONObject gameDataObj;
                    gameList.clear();
                    for(int i = 0; i < gameData.length(); i++) {
                        gameDataObj = gameData.getJSONObject(i);
                        middleman.getGameDetails(gameDataObj.getInt("game_id"), this);
                    }
                } else if(obj.has("games")) {   // szczegóły gier
                    JSONArray gameData = obj.getJSONArray("games");
                    JSONObject gameObj;
                    for(int i = 0; i < gameData.length(); i++) {
                        gameObj = gameData.getJSONObject(i);
                        Game game = new Game(
                                gameObj.getInt("id"),
                                gameObj.getInt("riddles"),
                                gameObj.getString("title"),
                                gameObj.getString("description")
                        );
                        gameList.add(game);
                        adapter.notifyDataSetChanged();
                    }
                } else if(obj.has("message") && obj.getString("message").contains("revoked")) {
                    finish();
                } else if(obj.has("access_token")) {
                    Log.d(TAG, "Access token refreshed");
                    // Token Refresh
                } else {
                    new AlertDialog.Builder(GameListActivity.this)
                            .setTitle(R.string.dialog_title_error)
                            .setMessage(R.string.dialog_body_unexpected_error_occured)
                            .setNeutralButton(R.string.button_ok, null)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Metoda wywoływana przy wybraniu gry z listy
     * @param game Dane wybranej gry
     */
    @Override
    public void onGameSelected(Game game) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.KEY_GAME_DATA, game);
        startActivity(intent);
    }

    /**
     * Metoda wywoływana podczas naciśnięcia dowolnego obiektu klasy View, w tym przycisków
     * @param v Widok (obiekt klasy View), który został naciśnięty
     */
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnJoin) {
            startActivityForResult(new Intent(this, ScannerActivity.class), CODE_QR_REQUEST);
        }
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
        if(requestCode == CODE_QR_REQUEST) {
            if(resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    URL uri = new URL(data.getStringExtra(ScannerActivity.KEY_DATA));
                    if(uri.toString().startsWith(APIDetails.ENDPOINT_MY_GAMES) && uri.toString().endsWith("start")) {
                        String[] urlParts = uri.getPath().split("/");
                        int gameId = Integer.parseInt(urlParts[urlParts.length - 2]);
                        Log.d(TAG, "Found game ID: " + gameId);
                        Toast.makeText(this, R.string.alert_joining_game, Toast.LENGTH_SHORT).show();
                        middleman.refreshToken(this);
                        middleman.startGame(gameId, this);
                    } else {
                        Log.d(TAG, "Game not found, URL: " + uri);
                    }
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Invalid QR content");
                    e.printStackTrace();
                } catch (AssertionError error) {
                    Log.d(TAG, "Empty data received");
                    error.printStackTrace();
                }
            }
        }
    }

    /**
     * Metoda wywoływana po naciśnięciu przycisku lub wykonaniu gestu "wstecz"
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(GameListActivity.this)
                .setTitle("Uwaga")
                .setMessage("Na pewno chcesz się wylogować?")
                .setPositiveButton("Tak", (dialog, which) -> {
                    middleman.logout(this);
                })
                .setNegativeButton("Nie", null)
                .show();
    }
}
