package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    EditText nameEditText, passwordEditText;
    Button createButton;
    TextView signInButton;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createButton = findViewById(R.id.createButton);
        signInButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //signInButton.setVisibility(View.INVISIBLE);

                String name = nameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)){
                    nameEditText.setError("Name is required");
                    passwordEditText.setError("Password is required");
                    return;
                }

                if (TextUtils.isEmpty(name)){
                    nameEditText.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (password.length() < 6){
                    passwordEditText.setError("Password must be at least 6 characters");
                    return;
                }

                createButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                // Registering the user

                fAuth.createUserWithEmailAndPassword(name + "@pizza.com", password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            signInButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}
