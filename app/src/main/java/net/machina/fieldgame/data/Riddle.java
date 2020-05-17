package net.machina.fieldgame.data;

import java.io.Serializable;

/**
 * Klasa przechowująca informację o obecnej zagadce
 * @author Patryk Ciepiela, Bartłomiej Gil
 */
public class Riddle implements Serializable {

    /**
     * Numer zagadki
     */
    private int riddleNo;

    /**
     * Opis zagadki
     */
    private String riddleDescription;

    /**
     * Szerokość geograficzna miejsca zawierającego przedmiot zagadki
     */
    private double riddleLatitude;

    /**
     * Długość geograficzna miejsca zawierającego przedmiot zagadki
     */
    private double riddleLongitude;

    /**
     * Promień obszaru poszukiwań
     */
    private int riddleRadius;

    /**
     * Opis przedmiotu zagadki zwracany przez Firebase ML Kit
     */
    private String riddleDominantObject;

    /**
     * Konstruktor obiektu
     * @param riddle_no Numer zagadki
     * @param description Opis zagadki
     * @param latitude Szerokość geograficzna miejsca zawierającego przedmiot zagadki
     * @param longitude Długość geograficzna miejsca zawierającego przedmiot zagadki
     * @param radius Promień obszaru poszukiwań
     * @param dominant_object Opis przedmiotu zagadki zwracany przez Firebase ML Kit
     */
    public Riddle(int riddle_no, String description, double latitude, double longitude, int radius, String dominant_object){
        this.riddleNo = riddle_no;
        this.riddleDescription = description;
        this.riddleLatitude = latitude;
        this.riddleLongitude = longitude;
        this.riddleRadius = radius;
        this.riddleDominantObject = dominant_object;
    }

    /**
     * Pobieranie numeru zagadki
     * @return {@link #riddleNo Numer zagadki}
     */
    public int getRiddleNo() {
        return riddleNo;
    }

    /**
     * Pobieranie opisu zagadki
     * @return {@link #riddleDescription Opis zagadki}
     */
    public String getRiddleDescription() {
        return riddleDescription;
    }

    /**
     * Pobieranie szerokośi geograficznej miejsca zawierającego przedmiot zagadki
     * @return {@link #riddleLatitude Szerokość geograficzna miejsca zawierającego przedmiot zagadki}
     */
    public double getRiddleLatitude() {
        return riddleLatitude;
    }

    /**
     * Pobieranie długości geograficznej miejsca zawierającego przedmiot zagadki
     * @return {@link #riddleLongitude Długość geograficzna miejsca zawierającego przedmiot zagadki}
     */
    public double getRiddleLongitude() {
        return riddleLongitude;
    }

    /**
     * Pobieranie promienia obszaru poszukiwań
     * @return {@link #riddleRadius Obszar poszukiwań}
     */
    public int getRiddleRadius() {
        return riddleRadius;
    }

    /**
     * Pobieranie opisu przedmiotu zagadki
     * @return {@link #riddleDominantObject Opis przedmiotu zagadki}
     */
    public String getRiddleDominantObject() {
        return riddleDominantObject;
    }
}
