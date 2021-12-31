package com.example.cse4086_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class  AdminProfileActivity  extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        TextView textView = findViewById(R.id.admin);
        textView.setText("Welcome "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail() + "!");

    }

    public void getAllPosts(View view) {
        Intent intent = new Intent(this, PostManager.class);
        startActivity(intent);
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                AdminProfileActivity.this);
        alert.setTitle("Hey!");
        alert.setMessage("Do you want to logout?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
        alert.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        alert.show();
    }

}