package com.example.cse4086_project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChangingActivity extends AppCompatActivity {

    private EditText password;
    private EditText newPassword;
    private EditText newPasswordRepeat;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_changing);
        password = findViewById(R.id.editTextTextPassword);
        newPassword = findViewById(R.id.editTextTextNewPassword);
        newPasswordRepeat = findViewById(R.id.editTextTextNewPasswordRepeat);

    }

    public void previousActivityForPassword(View view){
        finish();
    }

    public void savePasswordChanges(View view){
        String passwordString = password.getText().toString();
        String newPasswordString = newPassword.getText().toString();
        String newPasswordRepeatString = newPasswordRepeat.getText().toString();

        FirebaseUser user = auth.getCurrentUser();

        String email = user.getEmail();

        if(!passwordString.equals("")){
            AuthCredential credential = EmailAuthProvider.getCredential(email, passwordString);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    if(!newPasswordString.equals("") && newPasswordString.length() >= 6 && newPasswordString.equals(newPasswordRepeatString)){
                        user.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(PasswordChangingActivity.this, "Your password is changed!", Toast.LENGTH_SHORT).show();
                                    PasswordChangingActivity.super.onBackPressed();
                                }

                            }
                        });
                    }

                    if(!newPasswordString.equals(newPasswordRepeatString)){
                        Toast.makeText(PasswordChangingActivity.this, "New password repeat is wrong!", Toast.LENGTH_SHORT).show();
                    }
                    if(newPasswordString.length() < 6){
                        Toast.makeText(PasswordChangingActivity.this, "New password length must longer than or equal 6!", Toast.LENGTH_SHORT).show();
                    }
                }

                else{
                    Toast.makeText(PasswordChangingActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}