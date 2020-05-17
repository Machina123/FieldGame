package net.machina.fieldgame.data;

import java.io.Serializable;

/**
 * Klasa przechowująca dane o aktualnym stanie gry
 * @author Patryk Ciepiela, Bartłomiej Gil
 */
public class GameStatus implements Serializable {

    /**
     * Obecna zagadka
     */
    public int currentRiddle;

    /**
     * Informacja, czy dana gra została ukończona
     */
    public Boolean isFinished;

    /**
     * Konstruktor obiektu
     * @param currentRiddle Obecna zagadka
     * @param isFinished Informacja, czy gra została ukończona
     */
    public GameStatus(int currentRiddle, Boolean isFinished){
        this.currentRiddle = currentRiddle;
        this.isFinished = isFinished;
    }

    /**
     * Pobieranie informacji o obecnej zagadce
     * @return Numer obecnej zagadki
     */
    public int getCurrentRiddle() {
        return currentRiddle;
    }

    /**
     * Pobieranie informacji, czy gra została ukończona
     * @return Informacja, czy gra została ukończona (tak/nie)
     */
    public Boolean getFinished() {
        return isFinished;
    }
}
