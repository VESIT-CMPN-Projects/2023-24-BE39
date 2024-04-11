package com.example.aarogyamitra;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aarogyamitra.ml.BrainTumor;
import com.example.aarogyamitra.ml.SkinCancer;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class openSkinCancerActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap img;
    private TextView resultview;

    // Constants for image resizing
    private static final int TARGET_WIDTH = 28; // Adjust as needed
    private static final int TARGET_HEIGHT = 28; // Adjust as needed

    private static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_skin_cancer);

        imageView = findViewById(R.id.imageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button runModelButton = findViewById(R.id.runModelButton);
        resultview=findViewById(R.id.results);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        runModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img != null) {
                    try {
                        // Load your TensorFlow Lite model
                        SkinCancer model = SkinCancer.newInstance(openSkinCancerActivity.this);


                        // Create a TensorImage object and load the image
                        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                        tensorImage.load(img);

                        // Get the ByteBuffer from the TensorImage
                        ByteBuffer byteBuffer = tensorImage.getBuffer();

                        // Create a TensorBuffer for the input tensor
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, TARGET_WIDTH, TARGET_HEIGHT, 3}, DataType.FLOAT32);

                        // Load the ByteBuffer into the input TensorBuffer
                        inputFeature0.loadBuffer(byteBuffer);

                        // Perform inference
                        SkinCancer.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                        // Release model resources
                        model.close();
                        int val = outputFeature0.getIntArray()[0];

                        // Handle the output as needed
                        Log.d(TAG, "Output: " + outputFeature0.getFloatArray()[0]);
                        float p = argmax(outputFeature0.getFloatArray());
                        String predictionResult = getPredictionString((int) p);
                        Log.d(TAG, "Output: " + predictionResult);
                        resultview.setText(predictionResult);


                    } catch (IOException e) {
                        // Handle the exception
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // Resize the image to match the expected input size
                    img = Bitmap.createScaledBitmap(img, TARGET_WIDTH, TARGET_HEIGHT, true);
                    imageView.setImageBitmap(img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Function to get the prediction string
    private String getPredictionString(int predictedClass) {
        switch (predictedClass) {
            case 0:
                return "Melanocytic Nevi-Benign";
            case 1:
                return "Melanoma";
            case 2:
                return "Benign Keratosis";
            case 3:
                return "Basal Cell Carcinoma";
            case 4:
                return "Actinic Keratoses";
            case 5:
                return "Vascular Skin Lesions";
            case 6:
                return "Dermatofibroma";
            default:
                return "Unknown";
        }
    }

    // Function to get the index of the maximum value in an array
    private int argmax(float[] array) {
        int maxIndex = 0;
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                maxIndex = i;
                max = array[i];
            }
        }
        return maxIndex;
    }
}
