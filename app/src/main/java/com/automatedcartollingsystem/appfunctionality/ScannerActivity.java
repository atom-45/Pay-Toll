package com.automatedcartollingsystem.appfunctionality;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.automatedcartollingsystem.services.TollNotificationService;
import com.example.automatedcartollingsystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.camera.CameraSourceConfig;
import com.google.mlkit.vision.camera.CameraXSource;
import com.google.mlkit.vision.camera.DetectionTaskCallback;
import com.google.mlkit.vision.common.InputImage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    private Barcode barcode;
    private BarcodeScanner barcodeScanner;
    private BarcodeScannerOptions barcodeScannerOptions;
    private BarcodeScanning barcodeScanning;

    private String barcodeData;
    private static final int REQUEST_CAMERA_PERMISSION=201;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        previewView = findViewById(R.id.scannerCamera);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
               ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
               bindPreview(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        //CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        //Use both and toast and notifications to alert the user to
        //To-Do, 1st we have to make sure that the camera reads the QR code. Done
        //2. Translate the QR code into words and check if it reads the correct one. Done.
        //3. Use the words and send them into the data bases to gather the price of the toll
        //4. send the price of the toll to a java activity so that we can do the necessary deductions.

        //Key take aways: It does not take multiple barcodes simutaneously thus I h
        // I am leaving it the way it is. I will wait and do research about taking or scanning
        // multiple barcodes without going back and forth.
    }

    private void scanBarcode(InputImage image){

        BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_AZTEC,Barcode.FORMAT_CODABAR)
                .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(barcodeScannerOptions);
        //scanner.process(image);
        /*CameraSourceConfig cameraSourceConfig= new CameraSourceConfig.Builder(getApplicationContext(),
                scanner, task ->

                task.addOnSuccessListener(barcodes -> {
            for (Barcode barcode: barcodes){
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();
                String rawValue = barcode.getRawValue();
                int valueType = barcode.getValueType();

                if (valueType == Barcode.TYPE_TEXT) {
                    barcodeData = barcode.getRawValue();
                    Log.e("Barcode: ",barcodeData);
                }
            }
        }).addOnFailureListener(Throwable::printStackTrace))
                .setFacing(CameraSourceConfig.CAMERA_FACING_BACK)
                .build();

        CameraXSource cameraXSource = new CameraXSource(cameraSourceConfig,previewView);
        //CameraXSource cameraXSource = new CameraXSource();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScannerActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

        }
        cameraXSource.start();*/

        Task<List<Barcode>> result = scanner.process(image).addOnSuccessListener(
                barcodes -> {
                   for (Barcode barcode: barcodes){
                       Rect bounds = barcode.getBoundingBox();
                       Point[] corners = barcode.getCornerPoints();
                       String rawValue = barcode.getRawValue();
                       int valueType = barcode.getValueType();

                       if (valueType == Barcode.TYPE_TEXT) {
                           Intent intent = new Intent(this, TollNotificationService.class);
                           intent.putExtra(TollNotificationService.EXTRA_MESSAGE,rawValue);
                           startService(intent);
                           Log.e("Barcode",rawValue);
                       }else if(valueType == Barcode.FORMAT_CODABAR){
                           Log.e("Barcode",rawValue);
                       }
                   }
                }).addOnFailureListener(Throwable::printStackTrace);

    }
    //From my understanding this method simply allows me to use the camera.
    @RequiresApi(api = Build.VERSION_CODES.P)
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(Surface.ROTATION_90)
                .build();

        imageAnalysis.setAnalyzer(getMainExecutor(), image -> {
            try  {
                @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = image.getImage();

                if(mediaImage!=null){
                    InputImage inputImage = InputImage.fromMediaImage(
                            mediaImage, image.getImageInfo().getRotationDegrees());
                    scanBarcode(inputImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           // image.close();
        });
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector,imageAnalysis,preview);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}