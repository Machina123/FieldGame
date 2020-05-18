package net.machina.fieldgame.qrscan;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.util.List;

/**
 * Interfejs nasłuchujący listy wyników z bazy danych Firebase Vision.
 */
public interface BarcodeDataReceivedListener {
    /**
     * Abstrakcyjna klasa pozwalająca wykonywać operacje na liscie kodów kreskowych uzyskanych z bazy Firebase Vision.
     * @param label lista kodów kreskowych pobrana z bazy Firebase Vision
     */
    void onBarcodeDataReceived(List<FirebaseVisionBarcode> data);
}
