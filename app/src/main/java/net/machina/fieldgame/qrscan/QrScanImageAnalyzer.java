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

public class QrScanImageAnalyzer implements ImageAnalysis.Analyzer {
    private static final String TAG = "FieldGame/QrAnalyzer";
    private BarcodeDataReceivedListener barcodeDataReceivedListener;

    public void setBarcodeDataReceivedListener(BarcodeDataReceivedListener barcodeDataReceivedListener) {
        this.barcodeDataReceivedListener = barcodeDataReceivedListener;
    }

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
