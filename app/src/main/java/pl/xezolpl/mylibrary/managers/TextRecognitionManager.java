package pl.xezolpl.mylibrary.managers;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public abstract class TextRecognitionManager{
    private static String resultText = null;

    public static String getRecognizedTextFromBitmap(Bitmap bitmap, boolean removeNextLines){

        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer deviceRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        deviceRecognizer.processImage(visionImage)
                .addOnSuccessListener(firebaseVisionText -> {
                    resultText = firebaseVisionText.getText();
                    if (removeNextLines) {
                        resultText = resultText.replace('\n', ' ');
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
        return resultText;
    }
}
