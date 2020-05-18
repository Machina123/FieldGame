package net.machina.fieldgame;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import net.machina.fieldgame.imagelabeling.ImageCaptureCallback;
import net.machina.fieldgame.imagelabeling.ImageReceivedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 *  Klasa zajmująca sie pobieraniem obrazu z aparatu urządzenia, zapisaniem go w pamięci podręcznej i przekazaniem do dalszej obróbki.
 */
public class ImageLabelingActivity extends AppCompatActivity implements LifecycleOwner,  CameraXConfig.Provider, ImageReceivedListener {

    /**
     * Etykieta identyfikująca wpis w dzienniku.
     */
    private static final String TAG = "FieldGame/ImageLabeling";

    /**
     * Nazwa klucza pod którym znajdują sie dane przesywałe z tej klasy.
     */
    public static final String KEY_DATA = "labels";

    /**
     * Kod zapytania o przyznanie pozwolenia do korzystania z aparatu.
     */
    private int REQUEST_CODE_PERMISSIONS = 0xf1e1d;

    /**
     * Przechowuje status pozwolenia na korzystanie z aparatu urządzenia.
     */
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    /**
     * Zmienna przechowująca widok podglądu obrazu.
     */
    private PreviewView previewView;

    /**
     * Przycisk inicjujacy wykonanie zdjecia.
     */
    private Button btn;

    /**
     * Obiekt klasy {@link ImageCaptureCallback} odpowiedzialnej za przetwarzanie obrazu.
     */
    private ImageCaptureCallback callback;

    /**
     * Okno dialogowe pokazujące sie podczas oczekiwania na dane zwrócone z bazy danych.
     */
    private AlertDialog dialog;

    /**
     * Obiekt klasy udostępniającej obraz z aparatu.
     */
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    /**
     * Metoda wywoływana przy tworzeniu okna, inicjalizowane są w niej wszytkie parametry i wywoływane są wszystkie operacje które powinny sie wykonac przy starcie okna.
     * @param savedInstanceState zapisany stan aktywności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_labeling);
        if(!checkPermissionsGranted()) ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        callback = new ImageCaptureCallback();
        callback.setImageReceivedListener(this);
        previewView = findViewById(R.id.preview_view);
        btn = findViewById(R.id.btn_detect);

        dialog = new AlertDialog.Builder(ImageLabelingActivity.this).setMessage("Proszę czekać").setCancelable(false).create();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageCapture(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Metoda odpowiadająca za wyświetlanie podglądu obrazu z aparatu na ekranie telefonu oraz za zrobienia zdjęcia po kliknieciu przycisku.
     * @param cameraProvider referencja do klasy udostępniającej obraz z aparatu
     */
    private void bindImageCapture(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .setTargetResolution(new Size(1920,1080))
                .build();

        preview.setSurfaceProvider(previewView.getPreviewSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageCapture image = new ImageCapture.Builder()
                .setTargetResolution(new Size(1920,1080))
                .build();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                image.takePicture(ContextCompat.getMainExecutor(ImageLabelingActivity.this), callback);
            }
        });

        cameraProvider.unbindAll();
        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, image);
        } catch(Exception e) {
            Log.e(TAG, "Use case binding failed");
            e.printStackTrace();
        }
    }

    /**
     * Metoda sprawdza czy użytkownik zezwolił na korzystanie z aparatu urządzenia
     * @return zwraca odpowiedz czy pozwolenie zostało przyznane (tak/nie)
     */
    private boolean checkPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    /**
     * Metoda zwracająca domyślne ustawienia aparatu.
     * @return zwraca ustawiania aparatu
     */
    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    /**
     * Na podstawie otrzymanej listy etykiet tworzy nowy obiekt typu {@link ArrayList} oraz przesyła go do okna z którego zostało wywołanie rządanie do klasy.
     * @param label lista etykiet obrazów pobrana z bazy Firebase Vision
     */
    @Override
    public void onImageReceived(List<FirebaseVisionImageLabel> label) {
        ArrayList<String> labelsList = new ArrayList<>();
        if(label != null){
            for(FirebaseVisionImageLabel labels: label){
                labelsList.add(labels.getText());
            }
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(KEY_DATA, labelsList);
            setResult(RESULT_OK, resultIntent);
            finish();
            if(dialog.isShowing())
                dialog.dismiss();
        }
    }
}