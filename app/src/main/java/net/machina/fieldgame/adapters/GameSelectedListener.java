package net.machina.fieldgame.adapters;

import net.machina.fieldgame.data.Game;

/***
 * Interfejs nasłuchujący na wybraną przez użytkownika grę z listy
 */
public interface GameSelectedListener {
    /**
     * Metoda wywoływana po wybraniu gry z listy
     * @param game Dane wybranej gry
     */
    void onGameSelected(Game game);
}
