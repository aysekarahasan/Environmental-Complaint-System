package com.example.cse4086_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserNameChangingActivity extends AppCompatActivity {

    private EditText newUserName;
    private EditText password;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_changing);

        newUserName = findViewById(R.id.editTextNewNameSurname);
        password = findViewById(R.id.editTextPassword4);
    }

    public void previousActivityForUserName(View view){
        startActivity(new Intent(UserNameChangingActivity.this, SettingsActivity.class));
    }

    public void saveUserNameChanges(View view){
        String newUserNameString = newUserName.getText().toString();
        String passwordString = password.getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();

        if(!passwordString.equals("")){
            AuthCredential credential = EmailAuthProvider.getCredential(email, passwordString);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(newUserNameString).build();
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("Name: ", user.getDisplayName());
                                Toast.makeText(UserNameChangingActivity.this, "Name and surname are changed!",Toast.LENGTH_SHORT).show();
                                UserNameChangingActivity.super.onBackPressed();
                            }
                            else{
                                Toast.makeText(UserNameChangingActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(UserNameChangingActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}