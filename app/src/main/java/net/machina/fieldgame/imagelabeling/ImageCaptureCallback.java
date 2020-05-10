package net.machina.fieldgame.imagelabeling;

import android.media.Image;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import net.machina.fieldgame.connectingcheck.InternetCheck;

import java.util.List;

public class ImageCaptureCallback extends ImageCapture.OnImageCapturedCallback {

    private static final String TAG = "FieldGame/ImageLabeling";
    private ImageReceivedListener imageReceivedListener;

    public void setImageReceivedListener(ImageReceivedListener imageReceivedListener){
        this.imageReceivedListener = imageReceivedListener;
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
    public void onCaptureSuccess(@NonNull ImageProxy proxyImage) {
        int rotation = degreesToFirebaseRotation(proxyImage.getImageInfo().getRotationDegrees());
        Image mediaImage = proxyImage.getImage();
        FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

        new InternetCheck(new InternetCheck.Consumer() {
            @Override
            public void accept(boolean internet) {
                if(internet) {
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
                    Task<List<FirebaseVisionImageLabel>> result = labeler.processImage(image);
                    result.addOnSuccessListener((labels) -> imageReceivedListener.onImageReceived(labels));
                    result.addOnFailureListener(Exception::printStackTrace);
                    proxyImage.close();
                }
                else{
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
                    Task<List<FirebaseVisionImageLabel>> result = labeler.processImage(image);
                    result.addOnSuccessListener((labels) -> imageReceivedListener.onImageReceived(labels));
                    result.addOnFailureListener(Exception::printStackTrace);
                    proxyImage.close();
                }
            }
        });
    }

    @Override
    public void onError(@NonNull ImageCaptureException exception) {
        super.onError(exception);
    }
}
