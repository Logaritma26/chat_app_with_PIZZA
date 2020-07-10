package com.example.chat_app.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.login.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    OwnData ownData = OwnData.getInstance();

    Button button;
    ConstraintLayout layout_change_password;
    ConstraintLayout layout_seen_status;
    ConstraintLayout layout_invite_friends;

    Boolean is_editing = false;
    ImageView status_edit;
    ImageView status_check;
    EditText edit_status;
    TextView status;

    CardView pp_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        log_out_setup();
        layout_clicks();

        status_edit = findViewById(R.id.status_profile_edit);
        edit_status = findViewById(R.id.edit_status);
        status = findViewById(R.id.status_profile);
        status_check = findViewById(R.id.status_edit_check);

        status_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_editing = true;
                edit_status.setText(status.getText().toString());
                edit_status.setVisibility(View.VISIBLE);
                status.setVisibility(View.GONE);
                status_edit.setVisibility(View.GONE);
                status_check.setVisibility(View.VISIBLE);
            }
        });

        status_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText(edit_status.getText().toString());
                // TODO -> send data to db
                edit_status.setVisibility(View.GONE);
                status.setVisibility(View.VISIBLE);
                status_edit.setVisibility(View.VISIBLE);
                status_check.setVisibility(View.GONE);
                is_editing = false;
            }
        });


        pp_edit = findViewById(R.id.pp_image_profile_edit);
    }

    private void layout_clicks() {

        layout_change_password = findViewById(R.id.layout_2);
        layout_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, ChangePassword.class);
                startActivity(intent);
            }
        });

        layout_seen_status = findViewById(R.id.layout_1);
        layout_seen_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, SeenStatus.class);
                startActivity(intent);
            }
        });

        layout_invite_friends = findViewById(R.id.layout_3);
        layout_invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Send URL");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


    }

    private void log_out_setup() {
        button = findViewById(R.id.log_out_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
                sharedPref.edit().remove("username").apply();
                sharedPref.edit().clear().apply();
                //delete_player_id -> update notification (delete related notification id and go into opening page with intent)
                delete_player_id(ownData.getName());
            }
        });
    }

    private void delete_player_id(String own_username) {
        own_username += "@pizza.com";

        final DocumentReference reference_val = db.collection("user_val").document(own_username);
        final DocumentReference reference_nicks = db.collection("user_nicks").document(own_username);

        update_field_notification_id(reference_val, "notification_id", "none", reference_nicks, "notification", "none");
    }

    private void update_field_notification_id(DocumentReference reference1, String field_name1, String value1, DocumentReference reference2, String field_name2, String value2) {

        reference1
                .update(field_name1, value1)
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

        reference2
                .update(field_name2, value2)
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


        Intent intent_2 = new Intent(profile.this, MainActivity.class);
        finish();
        startActivity(intent_2);
    }

    @Override
    public void onBackPressed() {
        if (is_editing){
            edit_status.setVisibility(View.GONE);
            status.setVisibility(View.VISIBLE);
            status_edit.setVisibility(View.VISIBLE);
            status_check.setVisibility(View.GONE);
            is_editing = false;
        } else {
            super.onBackPressed();
        }


    }
}