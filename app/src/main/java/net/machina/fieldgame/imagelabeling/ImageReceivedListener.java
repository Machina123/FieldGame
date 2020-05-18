package net.machina.fieldgame.imagelabeling;

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import java.util.List;

/**
 * Interfejs nasłuchujący listy wyników z bazy danych Firebase Vision.
 */
public interface ImageReceivedListener {
    /**
     * Abstrakcyjna klasa pozwalająca wykonywać operacje na liście etykiet uzyskanych z bazy Firebase Vision
     * @param label lista etykiet obrazów pobrana z bazy Firebase Vision
     */
    void onImageReceived(List<FirebaseVisionImageLabel> label);
}