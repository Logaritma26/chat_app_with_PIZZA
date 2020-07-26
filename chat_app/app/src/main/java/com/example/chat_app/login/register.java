package com.example.chat_app.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.R;
import com.example.chat_app.fragments.fragments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    EditText nameEditText;
    EditText passwordEditText;
    Button createButton;
    ProgressBar progressBar;

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    private boolean check = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        createButton = findViewById(R.id.createButton);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_click();
            }
        });


    }

    private void register_click() {
        final String name = nameEditText.getText().toString().toLowerCase();
        final String password = passwordEditText.getText().toString();

        if (!is_empty(name, password)) {

            nameEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            createButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            // Registering the user
            fAuth.createUserWithEmailAndPassword(name + "@pizza.com", password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d("register", "onComplete: " + authResult);
                            send_data(name);
                            save_username_shared(name);
                            Intent intent = new Intent(register.this, MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("register", "failed : " + e);
                            Toast.makeText(register.this, "This username is already in use by another account.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            createButton.setVisibility(View.VISIBLE);
                            nameEditText.setVisibility(View.VISIBLE);
                            passwordEditText.setVisibility(View.VISIBLE);
                        }
                    });


        }
    }

    private void save_username_shared(String shared_username) {
        SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username",shared_username);
        editor.apply();
    }


    private void send_data(final String username) {

        init_oneSignal(username);

        final Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("status", "Available");
        data.put("pp", "");
        data.put("status_visibilty", true);
        data.put("pp_visibilty", true);
        data.put("seen", 0);


        db.collection("user_nicks").document(username + "@pizza.com")
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("data send token", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("data send token", "Error writing document", e);
                    }
                });


        final Map<String, Object> user_val = new HashMap<>();
        user_val.put("username", username);
        user_val.put("pp_url", "");
        user_val.put("status", "Available");
        user_val.put("seen", 0);


        db.collection("user_val").document(username + "@pizza.com")
                .set(user_val)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("data send token", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("data send token", "Error writing document", e);
                    }
                });


    }

    private boolean is_empty(String name, String password) {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password)) {
            nameEditText.setError("Name is required !");
            passwordEditText.setError("Password is required !");
            return true;
        }
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Email is required !");
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required !");
            return true;
        }
        if (nameEditText.length() > 15) {
            nameEditText.setError("Username must not be above 15 characters !");
            return true;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters !");
            return true;
        }


        return false;
    }

    private void init_oneSignal(String username) {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        final DocumentReference reference_val = db.collection("user_val").document(username + "@pizza.com");
        final DocumentReference reference_nicks = db.collection("user_nicks").document(username + "@pizza.com");

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                ContainerMethods.update_field(reference_val, "notification_id", userId);
                ContainerMethods.update_field(reference_nicks, "notification", userId);
            }
        });
    }

}
