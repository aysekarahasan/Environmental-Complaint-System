package com.example.cse4086_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText email, password, repeatedPassword, nameSurname;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameSurname = findViewById(R.id.id_register_name_surname);
        email = findViewById(R.id.id_register_email);
        password = findViewById(R.id.id_register_pass);
        repeatedPassword = findViewById(R.id.id_register_pass2);
    }
    public void goToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        String name = nameSurname.getText().toString();
        String email = RegisterActivity.this.email.getText().toString();
        String password = RegisterActivity.this.password.getText().toString();
        String password2 = repeatedPassword.getText().toString();
        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2)) {
            Toast.makeText(getApplicationContext(), "Please enter all required information", Toast.LENGTH_LONG).show();
        } else {
            if (email.contains("@admin.com")) {
                RegisterActivity.this.email.setError("Invalid email domain!");

            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Your account created successfully", Toast.LENGTH_LONG).show();
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                Objects.requireNonNull(firebaseAuth.getCurrentUser()).updateProfile(userProfileChangeRequest)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Your profile created successfully", Toast.LENGTH_LONG).show();
                                                Intent homeActivity = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(homeActivity);
                                                finish();
                                            }
                                        });
                            } else {
                                Toast.makeText(getApplicationContext(), "Error while account creation executing!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }

}