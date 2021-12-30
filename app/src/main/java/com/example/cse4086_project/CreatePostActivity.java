package com.example.cse4086_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity implements OnMapReadyCallback{
    private ImageView picture;
    private EditText description;
    private String latlngToDB;
    SupportMapFragment mapFragment;
    GoogleMap googleMap;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dbReference = db.getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        setupUI(findViewById(R.id.parent));
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapToDB);
        Button pictureButton = findViewById(R.id.pictureButton);
        picture = findViewById(R.id.ImageView);
        Button postButton = findViewById(R.id.postButton);
        description = findViewById(R.id.description);
        Button pickLocationButton = findViewById(R.id.choose_location_button);

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        pickLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseLocationActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = description.getText().toString();
                String location = latlngToDB;
                String image = picture.toString();

                HashMap<String, String> userMap = new HashMap<>();

                userMap.put("description", desc);
                userMap.put("location", location);
                userMap.put("image", image);
                dbReference.push().setValue(userMap);
                Toast.makeText(CreatePostActivity.this, "Complaint is posted", Toast.LENGTH_SHORT).show();
            }
        });

        this.mapFragment.getMapAsync(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            picture.setImageBitmap(bitmap);
        }else if(requestCode == 1){
            String lat = data.getStringExtra("latitude");
            String lng = data.getStringExtra("longitude");
            System.out.println("lat :" + lat);
            System.out.println("lng :" + lng);
            latlngToDB = lat + "," + lng;
            LatLng show = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            googleMap.addMarker(new MarkerOptions().position(show));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(show, 10));
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(CreatePostActivity.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}