package net.machina.fieldgame.data;

import java.io.Serializable;

public class GameStatus implements Serializable {

    public int currentRiddle;
    public Boolean isFinished;

    public GameStatus(int currentRiddle, Boolean isFinished){
        this.currentRiddle = currentRiddle;
        this.isFinished = isFinished;
    }

    public int getCurrentRiddle() {
        return currentRiddle;
    }

    public Boolean getFinished() {
        return isFinished;
    }
}
