package net.machina.fieldgame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.AspectRatio;
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

public class ScannerActivity extends AppCompatActivity implements LifecycleOwner, CameraXConfig.Provider, BarcodeDataReceivedListener {

    private final int REQUEST_CODE_PERMISSIONS = 0xf1e1d;
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private PreviewView viewfinder;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private QrScanImageAnalyzer analyzer;
    private static final String TAG = "FieldGame/QRScan";

    public static final String KEY_DATA = "qrdata";

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

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        int rotation = viewfinder.getDisplay().getRotation();
        Preview preview = new Preview.Builder()
//                .setTargetResolution(new Size(1920,1080))
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis analysisUseCase = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(1920,1080))
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(this), analyzer);

        cameraProvider.unbindAll();
        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase, preview);
            preview.setSurfaceProvider(viewfinder.createSurfaceProvider(camera.getCameraInfo()));
        } catch(Exception e) {
            Log.e(TAG, "Use case binding failed");
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

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

    private boolean checkPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @Override
    public void onBarcodeDataReceived(List<FirebaseVisionBarcode> data) {
        if(data != null && data.size() > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_DATA, data.get(0).getRawValue());
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
