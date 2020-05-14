package com.example.chat_app.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {

    EditText nameEditText;
    EditText passwordEditText;
    Button createButton;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createButton = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_click();
            }
        });


    }

    private void register_click() {
        final String name = nameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if (!is_empty(name, password)){

            createButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            // Registering the user
            fAuth.createUserWithEmailAndPassword(name + "@pizza.com", password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Log.d("register", "onComplete: " + task.getResult());
                        Toast.makeText(register.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d("register", "failed : " + task.getException());
                        Toast.makeText(register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private boolean is_empty(String name, String password) {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)){
            nameEditText.setError("Name is required");
            passwordEditText.setError("Password is required");
            return true;
        }
        if (TextUtils.isEmpty(name)){
            nameEditText.setError("Email is required");
            return true;
        }
        if (TextUtils.isEmpty(password)){
            passwordEditText.setError("Password is required");
            return true;
        }
        if (password.length() < 6){
            passwordEditText.setError("Password must be at least 6 characters");
            return true;
        }

        return false;
    }


}
