package net.machina.fieldgame.Data;

public class Riddle {

    private int RIDDLE_NO;
    private String RIDDLE_DESCRIPTION;
    private double RIDDLE_LATITUDE;
    private double RIDDLE_LONGITUDE;
    private int RIDDLE_RADIUS;
    private String RIDDLE_DOMINANT_OBJECT;

    public Riddle(int riddle_no, String description, double latitude, double longitude, int radius, String dominant_object){
        this.RIDDLE_NO = riddle_no;
        this.RIDDLE_DESCRIPTION = description;
        this.RIDDLE_LATITUDE = latitude;
        this.RIDDLE_LONGITUDE = longitude;
        this.RIDDLE_RADIUS = radius;
        this.RIDDLE_DOMINANT_OBJECT = dominant_object;
    }

    public int getRIDDLE_NO() {
        return RIDDLE_NO;
    }

    public String getRIDDLE_DESCRIPTION() {
        return RIDDLE_DESCRIPTION;
    }

    public double getRIDDLE_LATITUDE() {
        return RIDDLE_LATITUDE;
    }

    public double getRIDDLE_LONGITUDE() {
        return RIDDLE_LONGITUDE;
    }

    public int getRIDDLE_RADIUS() {
        return RIDDLE_RADIUS;
    }

    public String getRIDDLE_DOMINANT_OBJECT() {
        return RIDDLE_DOMINANT_OBJECT;
    }
}
