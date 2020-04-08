package net.machina.fieldgame.qrscan;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import java.util.List;

public interface BarcodeDataReceivedListener {
    void onBarcodeDataReceived(List<FirebaseVisionBarcode> data);
}
