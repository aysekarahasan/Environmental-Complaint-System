package com.example.cse4086_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private Button changingUserNameButton, changingPasswordButton;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changingUserNameButton = findViewById(R.id.change_user_name_button);
        changingUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUserName();
            }
        });
        changingPasswordButton = findViewById(R.id.change_password_button);
        changingPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    public void previousActivityForSettings(View view){
        startActivity(new Intent(SettingsActivity.this, AdminProfileActivity.class));
    }

    public void changeUserName(){
        Intent intent = new Intent(this, UserNameChangingActivity.class);
        startActivity(intent);
    }

    public void changePassword(){
        Intent intent = new Intent(this, PasswordChangingActivity.class);
        startActivity(intent);
    }

}
