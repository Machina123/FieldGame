package net.machina.fieldgame.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Singleton pośredniczący w połączeniach z serwerem
 */
public class FieldGameNetworkMiddleman {

    /**
     * Instancja klasy
     */
    private static FieldGameNetworkMiddleman instance;

    /**
     * Klient HTTP
     */
    private OkHttpClient httpClient;

    /**
     * Stała zawierająca opis typu danych JSON, wykorzystywana do przetwarzania zapytań wysyłanych na serwer
     */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Pobieranie instancji klasy
     * @return Instancja klasy przechowywana w polu {@link #instance}. W przypadku, gdy pole jest puste, tworzony i zwracany jest nowy obiekt.
     */
    public static FieldGameNetworkMiddleman getInstance() {
        if(instance == null) instance = new FieldGameNetworkMiddleman();
        return instance;
    }

    /**
     * Konstruktor klasy.
     * Tworzy instancję klienta HTTP oraz ustawia sposób przechowywania plików cookie.
     */
    private FieldGameNetworkMiddleman() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        JavaNetCookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        httpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu zalogowania użytkownika do gry
     * @param username Nazwa użytkownika
     * @param password Hasło
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void login(String username, String password, final OnDataReceivedListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_LOGIN)
                .method("POST", RequestBody.create(object.toString(), JSON))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu zarejestrowania użytkownika w grze
     * @param username Nazwa użytkownika
     * @param password Hasło
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void register(String username, String password, final OnDataReceivedListener listener) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_REGISTER)
                .method("POST", RequestBody.create(object.toString(), JSON))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu odświeżenia "żetonu dostępowego"
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void refreshToken(final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_REFRESH_TOKEN)
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu wylogowania użytkownika i unieważnienia "żetonu dostępowego"
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void logout(final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_LOGOUT)
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu pobrania szczegółów wybranej gry
     * @param gameId Identyfikator gry
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void getGameDetails(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getGameDetailEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu pobrania zagadek dostępnych w danej grze
     * @param gameId Identyfikator gry
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void getRiddlesForGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getRiddlesForGameEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu pobrania postępu użytkownika w danej grze
     * @param gameId Identyfikator gry
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void getProgressForGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getMyProgressForGameEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu dołączenia użytkownika do gry
     * @param gameId Identyfikator gry
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void startGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getStartGameEndpoint(gameId))
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu aktualizacji postępu użytkownika w danej grze
     * @param gameId Identyfikator gry
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void advanceGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getAdvanceGameEndpoint(gameId))
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie do serwera w celu pobrania postępu we wszystkich grach, do których uzytkownik dołączył
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    public void getMyProgress(final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_MY_GAMES)
                .get()
                .build();
        sendRequest(req, listener);
    }

    /**
     * Metoda wysyłająca zapytanie HTTP
     * @param req Zapytanie do wysłania
     * @param listener Referencja do klasy nasłuchującej odpowiedzi serwera
     */
    private void sendRequest(Request req, final OnDataReceivedListener listener) {
        try {
            httpClient.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    listener.onDataReceived(null);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    listener.onDataReceived(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onDataReceived(null);
        }
    }
}