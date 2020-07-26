package com.example.chat_app.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    AuthCredential credential;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    Button submit;
    OwnData ownData = OwnData.getInstance();

    EditText old_pass;
    EditText new_password;
    EditText verify_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        submit = findViewById(R.id.button_change);
        old_pass = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        verify_password = findViewById(R.id.verify_password);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check_if_not_empty()){
                    credential = EmailAuthProvider.getCredential(ownData.getName() + "@pizza.com", old_pass.getText().toString());
                    submit();
                }

            }
        });



    }

    private boolean check_if_not_empty() {

        Boolean result = true;

        if (old_pass.getText().toString().isEmpty()){
            result = false;
        }

        if (new_password.getText().toString().isEmpty()){
            result = false;
        }

        if (new_password.getText().toString().isEmpty()){
            result = false;
        }

        if (!new_password.getText().toString().equals(verify_password.getText().toString())){
            result = false;
            Toast.makeText(this, "Passwords don't match !", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private void submit() {

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    user.updatePassword(verify_password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("changing password", "Password updated");
                                Toast.makeText(ChangePassword.this, "Password changed succeed !", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Log.d("changing password", "Error password not updated");
                            }
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("changing password", "Error auth failed - " + e.getMessage());
                Toast.makeText(ChangePassword.this, "Password changing failure," + e.getMessage() + " !", Toast.LENGTH_SHORT).show();
            }
        });

    }
}