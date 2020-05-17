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
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions;

import net.machina.fieldgame.connectingcheck.InternetCheck;
import java.util.List;

/**
 * Klasa obsługująca analize i przetwarzanie obrazu otrzymanego robienia zdjęcia.
 */
public class ImageCaptureCallback extends ImageCapture.OnImageCapturedCallback {

    /**
     * Etykieta identyfikująca wpis w dzienniku.
     */
    private static final String TAG = "FieldGame/ImageLabeling";

    /**
     * Obiekt typu {@link ImageReceivedListener}.
     */
    private ImageReceivedListener imageReceivedListener;

    /**
     * Metoda inicjalizująca {@link ImageReceivedListener} dla obiektu tej klasy.
     * @param imageReceivedListener okiekt {@link ImageReceivedListener} który ma zostać przypisany do tego obiektu.
     */
    public void setImageReceivedListener(ImageReceivedListener imageReceivedListener){
        this.imageReceivedListener = imageReceivedListener;
    }

    /**
     * Metoda zwracająca obiekt typu FirebaseVisionImageMetadata zawierający orientację zdjęcia w zależności od
     * orientacji aparatu przy robieniu zdjęcia.
     * @param degrees orientację zdjęcia podanych w stopniach. Fukcja przyjmuje wartosci 0, 90, 180, 270
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
     * Jest wywoływana w momencie gdy zdjęcie zostanie zrobione poprawnie.
     * Przetwarza otrzymane zdjęcie na obiekt typu Image a następnie odpytuje baze Firebase o liste etykiet pasujących
     * do wykonanego zdjęcia i przekazuję ją do obiektu typu {@link ImageReceivedListener}.
     * W przypadku gdy operacja sie nie powiedzie fukcja zwraca wyjątek na standardowe wyjście diagnostyczne.
     * @param proxyImage obraz uzyskany podczas robienia zdjęcia
     */
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
                    FirebaseVisionCloudImageLabelerOptions option = new FirebaseVisionCloudImageLabelerOptions.Builder().setConfidenceThreshold(0.75f).build();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler(option);
                    Task<List<FirebaseVisionImageLabel>> result = labeler.processImage(image);
                    result.addOnSuccessListener((labels) -> imageReceivedListener.onImageReceived(labels));
                    result.addOnFailureListener(Exception::printStackTrace);
                    proxyImage.close();
                }
                else{
                    FirebaseVisionOnDeviceImageLabelerOptions option = new FirebaseVisionOnDeviceImageLabelerOptions.Builder().setConfidenceThreshold(0.75f).build();
                    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler(option);
                    Task<List<FirebaseVisionImageLabel>> result = labeler.processImage(image);
                    result.addOnSuccessListener((labels) -> imageReceivedListener.onImageReceived(labels));
                    result.addOnFailureListener(Exception::printStackTrace);
                    proxyImage.close();
                }
            }
        });
    }

    /**
     * Zwraca wyjątek w przypadku gdy zdjęcie nie zostanie przekazane do klasy.
     * @param exception wyjątek generowany podczas wystapienia błędu
     */
    @Override
    public void onError(@NonNull ImageCaptureException exception) {
        super.onError(exception);
    }
}
