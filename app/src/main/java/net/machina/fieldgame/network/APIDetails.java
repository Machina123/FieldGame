package net.machina.fieldgame.network;

/**
 * Klasa przechowująca informację o lokalizacji oraz punktach końcowych interfejsu API częśći serwerowej aplikacji
 */
public class APIDetails {
    /**
     * Adres bazowy interfejsu API gry
     */
    private static final String API_BASE = "https://fieldgameapi.herokuapp.com/";

    /**
     * Adres punktu końcowego metody rejestrującej użytkownika w grze
     */
    public static final String ENDPOINT_REGISTER = API_BASE + "register";

    /**
     * Adres punktu końcowego metody logującej użytkownika do aplikacji
     */
    public static final String ENDPOINT_LOGIN = API_BASE + "login";

    /**
     * Adres punktu końcowego metody wylogowującej użytkownika z aplikacji
     */
    public static final String ENDPOINT_LOGOUT = API_BASE + "logout";

    /**
     * Adres punktu końcowego metody odświeżającej "żeton dostępowy"
     */
    public static final String ENDPOINT_REFRESH_TOKEN = API_BASE + "token/refresh";

    /**
     * Adres punktu końcowego metody wyświetlającej gry, do których użytkownik dołączył
     */
    public static final String ENDPOINT_MY_GAMES = API_BASE + "mygames";

    /**
     * Pobieranie adresu punktu końcowego zwracającego szczegóły gry
     * @param gameId Identyfikator gry
     * @return Adres punktu końcowego zwracającego szczegóły wybranej gry
     */
    public static String getGameDetailEndpoint(int gameId) {
        return API_BASE + "games/" + gameId;
    }

    /**
     * Pobieranie adresu punktu końcowego zwracającego listę zagadek w wybranej grze
     * @param gameId Identyfikator gry
     * @return Adres punktu końcowego zwracającego listę zagadek w wybranej grze
     */
    public static String getRiddlesForGameEndpoint(int gameId) {
        return API_BASE + "games/" + gameId + "/riddles";
    }

    /**
     * Pobieranie adresu punktu końcowego zwracającego postęp aktualnie zalogowanego użytkownika w wybranej grze
     * @param gameId Identyfikator gry
     * @return Adres punktu końcowego postęp aktualnie zalogowanego użytkownika w wybranej grze
     */
    public static String getMyProgressForGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId;
    }

    /**
     * Pobieranie adresu punktu końcowego umożliwiającego dołączenie do gry przez aktualnie
     * zalogowanego użytkownika
     * @param gameId Identyfikator gry
     * @return Adres punktu końcowego umożliwiającego dołączenie do gry przez aktualnie
     * zalogowanego użytkownika
     */
    public static String getStartGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId + "/start";
    }

    /**
     * Pobieranie adresu punktu końcowego umożliwiającego aktualizowanie postępu w wybranej grze
     * przez aktualnie zalogowanego użytkownika
     * @param gameId Identyfikator gry
     * @return Adres punktu końcowego umożliwiającego aktualizowanie postępu w wybranej grze
     * przez aktualnie zalogowanego użytkownika
     */
    public static String getAdvanceGameEndpoint(int gameId) {
        return ENDPOINT_MY_GAMES + "/" + gameId + "/advance";
    }
}
