package net.machina.fieldgame.network;

/**
 * Interfejs wykorzystywany do nasłuchiwania odpowiedzi serwera
 */
public interface OnDataReceivedListener {
    /**
     * Metoda wywoływana po odebraniu odpowiedzi od serwera
     * @param result Odpowiedź serwera w formie tekstowej
     */
    void onDataReceived(String result);
}
