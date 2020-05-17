package net.machina.fieldgame.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Klasa przechowująca informacje o grze
 * @author Patryk Ciepiela, Bartłomiej Gil
 */
public class Game implements Serializable {

    /**
     * Identyfikator gry
     */
    private int gameID;

    /**
     * Ilość zagadek w grze
     */
    private int gameRiddleCount;

    /**
     * Tytuł gry
     */
    private String gameTitle;

    /**
     * Opis gry
     */
    private String gameDescription;

    /**
     * Konstruktor obiektu
     * @param gameID Identyfikator gry w bazie danych
     * @param gameRiddleCount Ilość zagadek w grze
     * @param gameTitle Tytuł gry
     * @param gameDescription Opis gry
     */
    public Game(int gameID, int gameRiddleCount, String gameTitle, String gameDescription) {
        this.gameID = gameID;
        this.gameRiddleCount = gameRiddleCount;
        this.gameTitle = gameTitle;
        this.gameDescription = gameDescription;
    }

    /**
     * Pobieranie identyfikatora gry
     * @return Identyfikator gry
     */
    public int getGameID() {
        return gameID;
    }

    /**
     * Pobieranie ilości zagadek w grze
     * @return Ilość zagadek w grze
     */
    public int getGameRiddleCount() {
        return gameRiddleCount;
    }

    /**
     * Pobieranie tytułu gry
     * @return Tytuł gry
     */
    public String getGameTitle() {
        return gameTitle;
    }

    /**
     * Pobieranie opisu gry
     * @return Opis gry
     */
    public String getGameDescription() {
        return gameDescription;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID=" + gameID +
                ", gameRiddleCount=" + gameRiddleCount +
                ", gameTitle='" + gameTitle + '\'' +
                ", gameDescription='" + gameDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameID == game.gameID &&
                gameRiddleCount == game.gameRiddleCount &&
                Objects.equals(gameTitle, game.gameTitle) &&
                Objects.equals(gameDescription, game.gameDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, gameRiddleCount, gameTitle, gameDescription);
    }
}
