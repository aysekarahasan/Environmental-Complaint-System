package com.example.cse4086_project;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChangePassActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    EditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        editEmail = findViewById(R.id.id_change_pass_email);
    }
    public void goToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
    public void changePass(View view) {
        String email = editEmail.getText().toString();
        if (!email.equals("")) {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePassActivity.this, "Check your email to change your password!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangePassActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            editEmail.setError("Enter your email correctly!");
    }
}
