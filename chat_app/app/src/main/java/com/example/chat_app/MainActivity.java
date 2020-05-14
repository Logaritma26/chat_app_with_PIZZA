package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_app.login.register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private EditText username;
    private EditText password;
    private TextView register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.create_account);


        mAuth = FirebaseAuth.getInstance();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username != null && password != null){
                    login(username.getText().toString(), password.getText().toString());
                } else {
                    Log.d("Login error", "null exception");
                    Toast.makeText(MainActivity.this, "Error occured !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.chat_app.login.register.class);
                startActivity(intent);
            }
        });



    }

    private void login(String username, String password) {
        if (!username.isEmpty() &&  !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("Login", "Login Failure", task.getException());
                                Toast.makeText(MainActivity.this, "Login failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                //updateUI(null);

                            }

                        }
                    });
        } else if (username.isEmpty() && password.isEmpty()){
            this.username.setError("This field cannot be empty.");
            this.password.setError("This field cannot be empty.");
        }
        else if (username.isEmpty()){
            this.username.setError("This field cannot be empty.");
        }
        else if (password.isEmpty()){
            this.password.setError("This field cannot be empty.");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            //updateUI(currentUser);
        }

    }
}
