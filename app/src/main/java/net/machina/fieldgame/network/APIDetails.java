package net.machina.fieldgame.network;

public class APIDetails {
    private static final String API_BASE = "https://fieldgameapi.herokuapp.com/";
    public static final String ENDPOINT_REGISTER = API_BASE + "register";
    public static final String ENDPOINT_LOGIN = API_BASE + "login";
    public static final String ENDPOINT_LOGOUT = API_BASE + "logout";
    public static final String ENDPOINT_REFRESH_TOKEN = API_BASE + "token/refresh";
    public static final String ENDPOINT_MY_GAMES = API_BASE + "mygames";

    public static String getGameDetailEndpoint(int gameId) {
        return API_BASE + "games/" + gameId;
    }

    public static String getRiddlesForGameEndpoint(int gameId) {
        return API_BASE + "games/" + gameId + "/riddles";
    }

    public static String getMyProgressForGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId;
    }

    public static String getStartGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId + "/start";
    }

    public static String getAdvanceGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId + "/advance";
    }
}
