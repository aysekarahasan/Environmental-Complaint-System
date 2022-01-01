package com.example.cse4086_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class NewPost extends AppCompatActivity {

    private Button uploadButton;
    private ImageView imageView;
    private ProgressBar progressBar;
    private DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("test");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    String currentPhotoPath;
    private EditText description;
    private EditText locText;
    private Button locButton;
    private LocationRequest locationRequest;
    public static final int PICK_IMAGE = 1;
    boolean taken = true;
    private String fName;
    private Uri fUri;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String username = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        uploadButton = findViewById(R.id.id_upload5);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        description = findViewById(R.id.description2);
        locText = findViewById(R.id.location_text);
        locButton = findViewById(R.id.button_get_location);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locButton.setOnClickListener(v -> getCurrentLocation());

        imageView.setOnClickListener(v -> {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose your image!");
            builder.setItems(options, (dialog, item) -> {

                if (options[item].equals("Take Photo")) {
                    taken = true;

                    // ASK CAMERA PERMISSION
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
                    } else {
                        takePhotoIntent();
                    }

                } else if (options[item].equals("Choose from Gallery")) {
                    taken = false;
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 4);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
        uploadButton.setOnClickListener(v -> {
            if (taken) {
                uploadImageToFirebase2(fName, fUri);
            } else if (imageUri != null) {
                uploadToFirebase(imageUri);
            } else {
                Toast.makeText(NewPost.this, "Please choose image!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void uploadToFirebase(Uri uri) {
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getExtension(uri));
        fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri1) {
                HashMap<String, String> postMap = new HashMap<>();
                Intent intent = getIntent();
                postMap.put("username", username);
                postMap.put("image", uri1.toString());
                postMap.put("description", description.getText().toString());
                //TODO
                postMap.put("location", locText.getText().toString());
                dbReference.push().setValue(postMap);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(NewPost.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            }
        })).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(NewPost.this, "Uploading Fail!", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImageToFirebase2(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(taskSnapshot -> {
            image.getDownloadUrl().addOnSuccessListener(uri -> {
                HashMap<String, String> postMap = new HashMap<>();
                postMap.put("username", username);
                postMap.put("image", uri.toString());
                postMap.put("description", description.getText().toString());
                postMap.put("location", locText.getText().toString());
                dbReference.push().setValue(postMap);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(NewPost.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            });

        }).addOnProgressListener(snapshot -> progressBar.setVisibility(View.VISIBLE)).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(NewPost.this, "Uploading Fail!", Toast.LENGTH_SHORT).show();
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation();
                } else {
                    openGPS();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            File f = new File(currentPhotoPath);
            imageView.setImageURI(Uri.fromFile(f));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            fName = f.getName();
            fUri = contentUri;
        }
        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(NewPost.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isGPSEnabled()) {
                LocationServices.getFusedLocationProviderClient(NewPost.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(NewPost.this)
                                        .removeLocationUpdates(this);
                                if (locationResult.getLocations().size() > 0) {
                                    int i = locationResult.getLocations().size() - 1;
                                    locText.setText("Latitude: " + locationResult.getLocations().get(i).getLatitude() + " Longitude: " + locationResult.getLocations().get(i).getLongitude());
                                }
                            }
                        }, Looper.getMainLooper());
            } else {
                openGPS();
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void openGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                Toast.makeText(NewPost.this, "GPS is opened.", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(NewPost.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        return ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void takePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image_m = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
                currentPhotoPath = image_m.getAbsolutePath();

                photoFile = image_m;
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "net.smallacademy.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }


}