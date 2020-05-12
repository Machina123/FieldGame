package net.machina.fieldgame.data;

import java.io.Serializable;
import java.util.Objects;

public class Game implements Serializable {
    private int gameID, gameRiddleCount;
    private String gameTitle, gameDescription;

    public Game(int gameID, int gameRiddleCount, String gameTitle, String gameDescription) {
        this.gameID = gameID;
        this.gameRiddleCount = gameRiddleCount;
        this.gameTitle = gameTitle;
        this.gameDescription = gameDescription;
    }

    public int getGameID() {
        return gameID;
    }

    public int getGameRiddleCount() {
        return gameRiddleCount;
    }

    public String getGameTitle() {
        return gameTitle;
    }

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
