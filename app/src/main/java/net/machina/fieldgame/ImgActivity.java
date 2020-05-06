package net.machina.fieldgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ImgActivity extends AppCompatActivity {

    CameraView cameraView;
    Button btnDetect;
    AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (CameraView)findViewByIs(R.id.camera_view);
        btnDetect = (Button)findViewById(R.id.btn_detect);
        waitingDialog = new SpotsDialog.Builder().setMessage("Czekaj...").setCancelable(false).build();

        cameraView.addCameraKitListener(new CameraKitListener()){
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent){

            }

            @Override
            public void onError(CameraKitError cameraKitError){

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage){
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                runDetector();
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo){

            }
        }
        btnDetect.setOnClickListener(new View.OnClickListener()){
            @Override
            public void onClick(Viev view){
                cameraView.start();
                cameraView.captureImage();
            }
        }

    }

    private void runDetector() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        new InternetChack(new InternetCheck.Customer()){
            public void accept(boolean internet){
                if(internet){
                    FirebaseVisionCloudDetectorOptions option = new FirebaseVisionCloudDetectorOptions.Builter().setMaxResults(1).build();
                    FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance().getvisionCloudLabelDetectior(opitons);
                    detector.detectImage(image).addOnSuccessListener(new onSuccessListener<List<FirebaseVisionCloudLabel>>(){
                        public void onSucess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabel){
                            processDataResult(firebaseVisionCloudLabels);
                        }
                    }).addOnFailureListener(new onFailureListener() {
                        public void onFailure(@NonNull Exception e){
                            Log.d("Error", e.getMessage());
                        }
                    })
                }
            }
        }
    }

    private void processDataResult(List<FirebaseVisionCloudLabel> firebaseVisonCloudLabels){
        for(FirebaseVisonCloudLabel label : firebaseVisonCloudLabels){
            Toast.makeText(this, "Result: " + label.getLabel(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }
}
