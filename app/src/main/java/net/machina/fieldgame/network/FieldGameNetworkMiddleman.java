package net.machina.fieldgame.network;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FieldGameNetworkMiddleman {
    private static FieldGameNetworkMiddleman instance;
    private OkHttpClient httpClient;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static FieldGameNetworkMiddleman getInstance() {
        if(instance == null) instance = new FieldGameNetworkMiddleman();
        return instance;
    }

    private FieldGameNetworkMiddleman() {
        httpClient = new OkHttpClient.Builder().build();
    }

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

    public void refreshToken(final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_REFRESH_TOKEN)
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    public void logout(final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.ENDPOINT_LOGOUT)
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    public void getGameDetails(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getGameDetailEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    public void getRiddlesForGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getRiddlesForGameEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    public void getProgressForGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getMyProgressForGameEndpoint(gameId))
                .get()
                .build();
        sendRequest(req, listener);
    }

    public void startGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getStartGameEndpoint(gameId))
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

    public void advanceGame(int gameId, final OnDataReceivedListener listener) {
        Request req = new Request.Builder()
                .url(APIDetails.getAdvanceGameEndpoint(gameId))
                .post(RequestBody.create("", MediaType.get("text/plain")))
                .build();
        sendRequest(req, listener);
    }

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