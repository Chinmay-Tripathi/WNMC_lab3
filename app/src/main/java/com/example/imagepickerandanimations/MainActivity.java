package com.example.imagepickerandanimations;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button btnPickImage, btnZoomIn, btnZoomOut, btnRotate, btnCrop, btnSaveImage;
    private EditText etRotationAngle, etCropPercentage;
    private Bitmap selectedBitmap;
    private float zoomFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnRotate = findViewById(R.id.btnRotate);
        btnCrop = findViewById(R.id.btnCrop);
        btnSaveImage = findViewById(R.id.btnSaveImage);
        etRotationAngle = findViewById(R.id.etRotationAngle);
        etCropPercentage = findViewById(R.id.etCropPercentage);

        // Open gallery to pick an image
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Zoom In on button click
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImage(1.2f);
            }
        });

        // Zoom Out on button click
        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImage(0.8f);
            }
        });

        // Rotate the image by specified angle
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateImage();
            }
        });

        // Crop image on button click
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        // Save image on button click
        btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                selectedBitmap = BitmapFactory.decodeFile(picturePath);
                imageView.setImageBitmap(selectedBitmap);
                cursor.close();
            }
        }
    }

    private void zoomImage(float factor) {
        if (selectedBitmap != null) {
            zoomFactor *= factor;
            imageView.setScaleX(zoomFactor);
            imageView.setScaleY(zoomFactor);
        } else {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    private void rotateImage() {
        if (selectedBitmap != null) {
            try {
                String rotationAngleStr = etRotationAngle.getText().toString();
                if (rotationAngleStr.isEmpty()) {
                    Toast.makeText(this, "Enter rotation angle", Toast.LENGTH_SHORT).show();
                    return;
                }
                float rotationAngle = Float.parseFloat(rotationAngleStr);

                // Rotate the image
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                Bitmap rotatedBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, selectedBitmap.getWidth(), selectedBitmap.getHeight(), matrix, true);
                imageView.setImageBitmap(rotatedBitmap);
                selectedBitmap = rotatedBitmap; // Update the selected bitmap after rotating
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid rotation angle", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    private void cropImage() {
        if (selectedBitmap != null) {
            try {
                String cropPercentageStr = etCropPercentage.getText().toString();
                if (cropPercentageStr.isEmpty()) {
                    Toast.makeText(this, "Enter crop percentage", Toast.LENGTH_SHORT).show();
                    return;
                }
                float cropPercentage = Float.parseFloat(cropPercentageStr);
                int width = selectedBitmap.getWidth();
                int height = selectedBitmap.getHeight();
                int newWidth = (int) (width * (cropPercentage / 100));
                int newHeight = (int) (height * (cropPercentage / 100));

                // Crop the image
                Bitmap croppedBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, newWidth, newHeight);
                imageView.setImageBitmap(croppedBitmap);
                selectedBitmap = croppedBitmap; // Update the selected bitmap after cropping
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid crop percentage", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        if (selectedBitmap != null) {
            FileOutputStream outStream = null;
            // Save the image to the gallery
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(), selectedBitmap, "Edited Image", "Edited image saved");
            Uri savedImageURI = Uri.parse(savedImageURL);
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show();
        }
    }
}



