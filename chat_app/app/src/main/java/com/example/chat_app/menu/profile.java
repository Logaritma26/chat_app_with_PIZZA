package com.example.chat_app.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat_app.ContainerMethods;
import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.fragments.fragments;
import com.example.chat_app.login.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class profile extends AppCompatActivity {

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirebaseStorage firebaseStorage;

    Uri imageData;

    OwnData ownData;

    Button button;
    ImageView pp;

    ConstraintLayout layout_change_password;
    ConstraintLayout layout_invite_friends;

    ConstraintLayout layout_seen_status;
    ImageView seen_image;
    TextView seen_text;
    ConstraintLayout seen_status;
    ConstraintLayout status_color_layout_1;
    ConstraintLayout status_color_layout_2;
    ConstraintLayout status_color_layout_3;
    List<ConstraintLayout> colors = new ArrayList<>();
    Boolean is_seen_editing = false;


    Boolean is_editing = false;
    ImageView status_edit;
    ImageView status_change_click_ok;
    EditText edit_status;
    TextView status;
    TextView char_left;
    TextView username;

    CardView pp_edit;

    boolean still_running_profile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ownData = OwnData.getInstance();
        init_elements();

        log_out_setup();
        change_password_invite_friends();
        status_edit();
        pp_edit();
        setup_seen_status();


    }

    private void pp_edit() {

        SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
        String url = sharedPref.getString("pp_url", "none");

        if (url.equals("none") && !ownData.getPic_url().equals("none") && !ownData.getPic_url().equals("")){
            sharedPref.edit().remove("pp_url").commit();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("pp_url", ownData.getPic_url());
            editor.commit();
        }

        if (!ownData.getPic_url().equals("none") && !ownData.getPic_url().equals("")) {
            Glide
                    .with(profile.this)
                    .load(ownData.getPic_url())
                    .centerCrop()
                    .into(pp);
        }
        pp_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void status_edit() {
        username = findViewById(R.id.username);
        username.setText(ownData.getName());

        status.setText(ownData.getStatus());
        edit_status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                char_left.setText(String.valueOf(50 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        status_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_editing = true;
                username.setVisibility(View.GONE);
                edit_status.setText(status.getText().toString());
                edit_status.setVisibility(View.VISIBLE);
                status.setVisibility(View.GONE);
                status_edit.setVisibility(View.GONE);
                status_change_click_ok.setVisibility(View.VISIBLE);
                char_left.setVisibility(View.VISIBLE);
                char_left.setText(String.valueOf(50 - status.getText().toString().length()));
            }
        });

        status_change_click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_status.getText().toString().length() < 51) {
                    status.setText(edit_status.getText().toString());
                    ContainerMethods.change_status(db, edit_status.getText().toString());
                    edit_status.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    status_edit.setVisibility(View.VISIBLE);
                    status_change_click_ok.setVisibility(View.GONE);
                    char_left.setVisibility(View.GONE);
                    ownData.setStatus(edit_status.getText().toString());
                    username.setVisibility(View.VISIBLE);
                    is_editing = false;
                } else {
                    Toast.makeText(profile.this, "Status can't be longer than 50 character.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init_elements() {
        pp = findViewById(R.id.pp_profile);
        pp_edit = findViewById(R.id.pp_image_profile_edit);
        status_edit = findViewById(R.id.status_profile_edit);
        edit_status = findViewById(R.id.edit_status);
        status = findViewById(R.id.status_profile);
        char_left = findViewById(R.id.char_left);

        status_change_click_ok = findViewById(R.id.status_edit_check);
        status_color_layout_1 = findViewById(R.id.status_color_layout_1);
        status_color_layout_2 = findViewById(R.id.status_color_layout_2);
        status_color_layout_3 = findViewById(R.id.status_color_layout_3);
        colors.add(status_color_layout_1);
        colors.add(status_color_layout_2);
        colors.add(status_color_layout_3);
        seen_status = findViewById(R.id.seen_status_change);
        seen_image = findViewById(R.id.seen_status_image);
        seen_text = findViewById(R.id.seen_status_text);
        layout_seen_status = findViewById(R.id.layout_1);
    }

    private void setup_seen_status() {

        int own_seen = ownData.getSeen();
        switch (own_seen) {
            case 0:
                seen_image.setImageResource(R.drawable.ic_dynamic_group);
                seen_text.setText("Dynamic");
                break;

            case 1:
                seen_image.setImageResource(R.drawable.ic_moon);
                seen_text.setText("Night");
                break;

            case 2:
                seen_image.setImageResource(R.drawable.ic_invisible);
                seen_text.setText("Invisible");
                break;

            default:
                break;
        }

        layout_seen_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_editing) {
                    if (!is_seen_editing) {
                        is_seen_editing = true;
                        make_layouts_visible(true);
                    } else {
                        is_seen_editing = false;
                        make_layouts_visible(false);
                    }
                }
            }
        });

        status_color_clicks();


    }

    private void status_color_clicks() {
        status_color_layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownData.setSeen(0);
                seen_image.setImageResource(R.drawable.ic_dynamic_group);
                seen_text.setText("Dynamic");
                ContainerMethods.change_seen_status(db, 0);
                //ContainerMethods.update_seen_chats_get_friends(db, 0);
                ContainerMethods.change_last_seen_to(db, 0);
                is_seen_editing = false;
                gone_timer(2);
            }
        });

        status_color_layout_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownData.setSeen(1);
                seen_image.setImageResource(R.drawable.ic_moon);
                seen_text.setText("Night");
                ContainerMethods.change_seen_status(db, 1);
                ContainerMethods.change_last_seen_to(db, 2);
                //ContainerMethods.update_seen_chats_get_friends(db, 1);
                is_seen_editing = false;
                gone_timer(2);
            }
        });

        status_color_layout_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownData.setSeen(2);
                seen_image.setImageResource(R.drawable.ic_invisible);
                seen_text.setText("Invisible");
                ContainerMethods.change_seen_status(db, 2);
                ContainerMethods.change_last_seen_to(db, 3);
                //ContainerMethods.update_seen_chats_get_friends(db, 2);
                is_seen_editing = false;
                gone_timer(2);
            }
        });

    }

    private void make_layouts_visible(boolean show) {
        if (show) {
            seen_status.setVisibility(View.VISIBLE);
            layout_change_password.setVisibility(View.GONE);
            layout_invite_friends.setVisibility(View.GONE);
            visible_timer(0);
        } else {
            gone_timer(2);
        }
    }

    private void gone_timer(final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (index == -1) {
                            gone_timer(index - 1);
                        } else if (index == -2) {
                            seen_status.setVisibility(View.GONE);
                            layout_change_password.setVisibility(View.VISIBLE);
                            layout_invite_friends.setVisibility(View.VISIBLE);
                        } else {
                            colors.get(index).setVisibility(View.GONE);
                            gone_timer(index - 1);
                        }
                    }
                });
            }
        }, 50);
    }

    private void visible_timer(final int index) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (index == 3) {
                        } else {
                            colors.get(index).setVisibility(View.VISIBLE);
                            visible_timer(index + 1);
                        }
                    }
                });
            }
        }, 20);
    }

    private void change_password_invite_friends() {

        layout_change_password = findViewById(R.id.layout_2);
        layout_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_editing) {
                    Intent intent = new Intent(profile.this, ChangePassword.class);
                    startActivity(intent);
                }
            }
        });

        layout_invite_friends = findViewById(R.id.layout_3);
        layout_invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_editing) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageData = data.getData();
            upload();

            Bitmap selectedImage;
            try {
                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    selectedImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(profile.this.getContentResolver(), imageData));
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(profile.this.getContentResolver(), imageData);
                }
                pp.setImageBitmap(selectedImage);
            } catch (Exception ignored) {
            }

        }
    }

    public void delete_previous_pic() {
        if (!ownData.getPic_url().equals("")) {
            StorageReference newReference = FirebaseStorage.getInstance().getReference(ownData.getName());
            newReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("deleting previous pp", "onComplete: ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("deleting previous pp", "onFailure: " + e.getMessage());
                }
            });

        }
    }

    public void upload() {

        //delete_previous_pic();

        if (imageData != null) {
            storageReference.child("ProfilePictures").child(ownData.getName()).putFile(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newReference = FirebaseStorage.getInstance().getReference("ProfilePictures").child(ownData.getName());
                            newReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("get download url", "onSuccess: ");
                                            String url = uri.toString();
                                            ownData.setPic_url(url);
                                            ContainerMethods.send_download_url(db, url);

                                            SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
                                            sharedPref.edit().remove("pp_url").commit();
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("pp_url", url);
                                            editor.commit();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("get download url", "onFailure: " + e.getMessage());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

    }


    @Override
    public void onBackPressed() {
        if (is_editing) {
            edit_status.setVisibility(View.GONE);
            status.setVisibility(View.VISIBLE);
            status_edit.setVisibility(View.VISIBLE);
            status_change_click_ok.setVisibility(View.GONE);
            char_left.setVisibility(View.GONE);
            is_editing = false;
        } else {
            still_running_profile = true;
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ContainerMethods.set_last_seen_if(db,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!still_running_profile){
            ContainerMethods.set_last_seen_if(db,1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!still_running_profile){
            ContainerMethods.set_last_seen_if(db,1);
        }
    }


}