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
     * Data rozpoczęcia gry
     */
    public String gameStart;

    /**
     *  Data zakończenia gry
     */
    public String gameEnd;

    /**
     * Konstruktor obiektu
     * @param currentRiddle Obecna zagadka
     * @param isFinished Informacja, czy gra została ukończona
     * @param gameStart Informacje o dacie rozpoczęcia gry
     * @param gameEnd Informacja o dacie zakończenia gry
     */
    public GameStatus(int currentRiddle, Boolean isFinished, String gameStart, String gameEnd){
        this.currentRiddle = currentRiddle;
        this.isFinished = isFinished;
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
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

    /**
     * Pobieranie informacji o dacie rozpoczęcia gry
     * @return Data rozpoczęcia gry w postaci napisu
     */
    public String getGameStart(){
        return gameStart;
    }

    /**
     * Pobieranie informacji o dacie zakonczenia gry
     * @return Data zakończenia gry w postaci napisu
     */
    public String getGameEnd(){
        return gameEnd;
    }
}
