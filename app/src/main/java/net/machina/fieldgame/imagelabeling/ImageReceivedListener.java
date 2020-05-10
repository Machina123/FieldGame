package net.machina.fieldgame.imagelabeling;

import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import java.util.List;

public interface ImageReceivedListener {
    void onImageReceived(List<FirebaseVisionImageLabel> label);
}