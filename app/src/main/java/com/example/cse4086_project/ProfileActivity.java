package com.example.cse4086_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private TextView textView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        textView = findViewById(R.id.id_user_inf);
        textView.setText("It's "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()+"!");

    }

    public void signOutFromSystem(View view) {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                ProfileActivity.this);
        alert.setTitle("Hey!");
        alert.setMessage("Do you want to sign out?");
        alert.setPositiveButton("yes", (dialogInterface, i) -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        alert.setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.show();
    }

    public void createPost(View view) {
        Intent intent = new Intent(this, NewPost.class);
        startActivity(intent);
    }

    public void showMyPosts(View view) {
        Intent intent = new Intent(this, UserPostManager.class);
        startActivity(intent);
    }

    public void openProfileSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}