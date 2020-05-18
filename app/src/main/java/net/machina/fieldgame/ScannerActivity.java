package net.machina.fieldgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import net.machina.fieldgame.qrscan.BarcodeDataReceivedListener;
import net.machina.fieldgame.qrscan.QrScanImageAnalyzer;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Klasa zajmująca sie pobieraniem obrazu z aparatu urządzenia i wysyłaniem go do analizy pod kątem obecności kodu QR.
 *
 */
public class ScannerActivity extends AppCompatActivity implements LifecycleOwner, CameraXConfig.Provider, BarcodeDataReceivedListener {

    /**
     * Kod zapytania o przyznanie pozwolenia do korzystania z aparatu.
     */
    private final int REQUEST_CODE_PERMISSIONS = 0xf1e1d;

    /**
     * Przechowuje status pozwolenia na korzystanie z aparatu urządzenia.
     */
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    /**
     * Zmienna przechowująca widok podglądu obrazu.
     */
    private PreviewView viewfinder;

    /**
     * Obiekt klasy udostępniającej obraz z aparatu.
     */
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    /**
     * Obiekt klasy {@link QrScanImageAnalyzer} odpowiadającej za analizowanie obrazu i odczytanie z niego kodu QR
     */
    private QrScanImageAnalyzer analyzer;

    /**
     * Etykieta identyfikująca wpis w dzienniku.
     */
    private static final String TAG = "FieldGame/QRScan";

    /**
     * Nazwa klucza pod którym znajdują sie dane przesywałe z tej klasy.
     */
    public static final String KEY_DATA = "qrdata";

    /**
     * Metoda wywoływana przy tworzeniu okna, inicjalizowane są w niej wszytkie parametry i wywoływane są wszystkie operacje które powinny sie wykonac przy starcie okna.
     * @param savedInstanceState zapisany stan aktywności
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        if(!checkPermissionsGranted()) ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        viewfinder = findViewById(R.id.scan_preview);

        analyzer = new QrScanImageAnalyzer();
        analyzer.setBarcodeDataReceivedListener(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // nie powinno mieć miejsca, ale...
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

    }

    /**
     * Metoda odpowiadająca za wyświetlanie podglądu obrazu z aparatu na ekranie telefonu oraz za przesłanie obraz do klasy analizującej go pod kątem tego czy znajduje sie tam kod QR, w przypadku kiedy
     * kod QR zostanie wykryty zczytuje z niego dane.
     * @param cameraProvider referencja do klasy udostępniającej obraz z aparatu
     */
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        int rotation = viewfinder.getDisplay().getRotation();
        Preview preview = new Preview.Builder()
                .setTargetResolution(new Size(1920,1080))
                .setTargetRotation(rotation)
                .build();

        preview.setSurfaceProvider(viewfinder.getPreviewSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis analysisUseCase = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1920,1080))
                .setTargetRotation(rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(this), analyzer);

        cameraProvider.unbindAll();
        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase);
//            preview.setSurfaceProvider(viewfinder.createSurfaceProvider(camera.getCameraInfo()));
        } catch(Exception e) {
            Log.e(TAG, "Use case binding failed");
            e.printStackTrace();
        }

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
     * Sprawdza czy pozwolenie na korzystanie z aparatu urządzenia zostało przyznane i wyświetla odpowiedni komunikat w zależnościod wyboru użytkownika.
     * @param requestCode kod zezwolenia które chcemy uzyskać
     * @param permissions tablica przechowująca informacje o przyznanych pozwoleniach
     * @param grantResults przechowuje stan uprawnienia (udzielone/ nie udzielone)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if(checkPermissionsGranted()) Toast.makeText(this, "Permissions OK", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
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
     *  Metoda odbierająca dane z analizatora kodów QR.
     * @param data dane przekazane z analizatora
     */
    @Override
    public void onBarcodeDataReceived(List<FirebaseVisionBarcode> data) {
        if(data != null && data.size() > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_DATA, data.get(0).getRawValue());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
