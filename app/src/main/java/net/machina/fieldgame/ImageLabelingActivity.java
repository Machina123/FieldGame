package net.machina.fieldgame;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.ImageCapture;

import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.app.AlertDialog;

import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.common.util.concurrent.ListenableFuture;

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;


import net.machina.fieldgame.imagelabeling.ImageCaptureCallback;
import net.machina.fieldgame.imagelabeling.ImageReceivedListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

import dmax.dialog.SpotsDialog;

public class ImageLabelingActivity extends AppCompatActivity implements LifecycleOwner,  CameraXConfig.Provider, ImageReceivedListener {

    private static final String TAG = "FieldGame/ImageLabeling";
    private int REQUEST_CODE_PERMISSIONS = 0xf1e1d;
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private PreviewView previewView;
    private Button btn;
    private ImageCaptureCallback callback;
    AlertDialog dialog;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!checkPermissionsGranted()) ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        callback = new ImageCaptureCallback();
        callback.setImageReceivedListener(this);
        previewView = findViewById(R.id.preview_view);
        btn = findViewById(R.id.btn_detect);


        dialog = new SpotsDialog.Builder().setMessage("Proszę czekać ...").setCancelable(false).build();

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageCapture(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private ImageLabelingActivity getActivity(){
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void bindImageCapture(ProcessCameraProvider cameraProvider) {
        int rotation = previewView.getDisplay().getRotation();
        Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).setTargetRotation(rotation)
                .build();


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageCapture image = new ImageCapture.Builder().setTargetAspectRatio(AspectRatio.RATIO_16_9).setTargetRotation(rotation).build();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                image.takePicture(ContextCompat.getMainExecutor(getActivity()), callback);
            }
        });

        cameraProvider.unbindAll();
        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));

        } catch(Exception e) {
            Log.e(TAG, "Use case binding failed");
            e.printStackTrace();
        }
    }

    private boolean checkPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    @Override
    public void onImageReceived(List<FirebaseVisionImageLabel> label) {
        if(label != null){
            for(FirebaseVisionImageLabel labels: label){
                Toast.makeText(this, "Result" + labels.getText() + " " + labels.getConfidence(), Toast.LENGTH_SHORT).show();
            }
            if(dialog.isShowing())
                dialog.dismiss();
        }
    }
}