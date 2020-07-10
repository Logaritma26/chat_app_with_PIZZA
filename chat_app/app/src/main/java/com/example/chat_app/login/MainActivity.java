package com.example.chat_app.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private EditText username;
    private EditText password;
    private TextView register;
    private TextView login_error;
    private Guideline guideline;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        login_error = findViewById(R.id.login_error);
        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        register = findViewById(R.id.create_account);
        guideline = findViewById(R.id.guideline4);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



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
            final String shared_username = username;
            username += "@pizza.com";
            final String name = username;
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Login", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                ContainerMethods.get_own_data(db, mAuth);
                                ContainerMethods.get_own_friends(db, mAuth);
                                ContainerMethods.get_own_sent_requests(db, mAuth);
                                renew_player_id(name);
                                save_username_shared(shared_username);
                                Intent intent = new Intent(MainActivity.this, fragments.class);
                                finish();
                                startActivity(intent);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d("Login", "Login Failure", task.getException());
                                login_error.setVisibility(View.VISIBLE);
                                //updateUI(null);
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) guideline.getLayoutParams();
                                params.guidePercent = 0.82f;
                                guideline.setLayoutParams(params);
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

    private void save_username_shared(String shared_username) {
        SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username",shared_username);
        editor.apply();
    }

    private void renew_player_id(String name) {
        OneSignal.startInit(MainActivity.this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        final DocumentReference reference_val = db.collection("user_val").document(name);
        final DocumentReference reference_nicks = db.collection("user_nicks").document(name);



        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                update_field(reference_val, "notification_id", userId);
                update_field(reference_nicks, "notification", userId);
            }
        });
    }

    private void update_field(DocumentReference reference, String field_name, String value){

        reference
                .update(field_name, value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("renew player id", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("renew player id", "Error updating document", e);
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            ContainerMethods.get_own_data(db, mAuth);
            ContainerMethods.get_own_friends(db, mAuth);
            ContainerMethods.get_own_sent_requests(db, mAuth);
            Intent intent = new Intent(MainActivity.this, fragments.class);
            finish();
            startActivity(intent);
        }

    }
}
