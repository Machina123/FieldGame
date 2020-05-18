package net.machina.fieldgame.qrscan;

import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.util.List;

/**
 * Klasa obsługująca analize i przetwarzanie kodów QR.
 */
public class QrScanImageAnalyzer implements ImageAnalysis.Analyzer {

    /**
     * Etykieta identyfikująca wpis w dzienniku.
     */
    private static final String TAG = "FieldGame/QrAnalyzer";

    /**
     * Obiekt typu {@link BarcodeDataReceivedListener}.
     */
    private BarcodeDataReceivedListener barcodeDataReceivedListener;

    /**
     * Metoda inicjalizująca {@link BarcodeDataReceivedListener} dla obiektu tej klasy.
     * @param barcodeDataReceivedListener {@link BarcodeDataReceivedListener} który ma zostać przypisany do tego obiektu.
     */
    public void setBarcodeDataReceivedListener(BarcodeDataReceivedListener barcodeDataReceivedListener) {
        this.barcodeDataReceivedListener = barcodeDataReceivedListener;
    }

    /**
     * Metoda zwracająca obiekt typu FirebaseVisionImageMetadata zawierający orientację zdjęcia w zależności od
     * orientacji aparatu przy robieniu zdjęcia.
     * @param degrees orientacja zdjęcia podana w stopniach. Fukcja przyjmuje wartosci 0, 90, 180, 270
     * @return         obiekt typu FirebaseVisionImageMetadata zawierajacy orientację zdjęcia w stopniach
     */
    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }

    /**
     * Jest wywoływana w momencie uzyskania obrazu kodu QR z klasy {@link net.machina.fieldgame.ScannerActivity}.
     * Przetwarza otrzymane zdjęcie na obiekt typu Image a następnie dekoduje kod QR na zdjęciu i przekazuję do obiektu typu {@link BarcodeDataReceivedListener}.
     * W przypadku gdy operacja sie nie powiedzie funkcja zwraca wyjątek na standardowe wyjście diagnostyczne.
     * @param proxyImage obraz uzyskany z widoku aparatu
     */
    @Override
    @androidx.camera.core.ExperimentalGetImage
    public void analyze(@NonNull ImageProxy imageProxy) {
//        Log.d(TAG, "QrScanImageAnalyzer::analyze() executed");
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        int rotationDegrees = degreesToFirebaseRotation(imageProxy.getImageInfo().getRotationDegrees());
        Image mediaImage = imageProxy.getImage();
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotationDegrees);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image);
        result.addOnSuccessListener((barcodes) -> barcodeDataReceivedListener.onBarcodeDataReceived(barcodes));
        result.addOnFailureListener(Exception::printStackTrace);
        imageProxy.close();
    }
}
