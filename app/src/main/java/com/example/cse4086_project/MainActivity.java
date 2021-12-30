package com.example.cse4086_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //mail: mobildevelopment2021@gmail.com
    //şifre: mobil123
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText emailEditText;
    private EditText passwordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEditText = findViewById(R.id.id_register_email);
        passwordEditText = findViewById(R.id.id_register_pass);

    }

    public void goToChangePass(View view) {
        Intent intent = new Intent(this, ChangePassActivity.class);
        startActivity(intent);
    }


    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        String emailAddress = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (emailAddress.equals("")) {
            emailEditText.setError("Please enter your email address!");
        } else if (password.equals("")) {
            passwordEditText.setError("Please enter your password!");
        } else {
            if (emailAddress.contains("@admin.com")) {
                firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), AdminProfileActivity.class));
                        Toast.makeText(MainActivity.this, "You logged in successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Please try again!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), NewPost.class);
                            intent.putExtra("email", emailAddress);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "You logged in successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Please try again!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}