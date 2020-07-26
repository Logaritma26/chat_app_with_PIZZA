package com.example.chat_app.chat_page;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.chat_app.ContainerMethods;
import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.AbstractMakeClickAppCompat;
import com.example.chat_app.click_manager.Recycler_Click;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Date;


public class ChatPage extends AbstractMakeClickAppCompat implements Recycler_Click {


    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference messages_reference;
    FirebaseFirestore db;

    OwnData ownData;

    private String username = null;
    private String pp_url = null;

    TextView last_seen;
    ImageView last_seen_icon;
    TextView username_text;
    ImageView ppicture;
    FloatingActionButton fab;

    EditText message;
    Button back_button;

    RecyclerView recyclerView;
    ChatFireAdapter adapter;

    boolean still_running_chat = false;
    boolean is_online = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        change_notbar_color();
        getExtras();
        setup_pp();

        ownData = OwnData.getInstance();
        messages_reference = firebaseFirestore.collection("user_val")
                .document(ownData.getName() + "@pizza.com").collection("chats")
                .document(username + "@pizza.com").collection(username + "@pizza.com");

        db = FirebaseFirestore.getInstance();

        setup_fab();
        setup_toolbar();
        setup_fire_recycler();

        last_seen = findViewById(R.id.last_seen);
        last_seen_icon = findViewById(R.id.last_seen_icon);

        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        listen_seen();
    }

    private void change_notbar_color() {
        Window window = ChatPage.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChatPage.this, R.color.grif7));
    }

    private void listen_seen() {
        db.collection("user_val").document(username + "@pizza.com")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("listening last seen", "Listen failed." + error.getMessage(), error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            Log.d("listening last seen", "Current succeed");

                            Date date = new Date(System.currentTimeMillis());
                            Timestamp time = new Timestamp(date);

                            Long diff = null;
                            Timestamp time_last = value.getTimestamp("time_last_seen");
                            if (time_last != null){
                                diff = time.getSeconds() - time_last.getSeconds();
                                Log.d("diff time", "onEvent: "+ diff.intValue());

                            }


                            int last = value.getLong("last_seen").intValue();
                            switch (last){
                                case 0:
                                    last_seen.setText("Online");
                                    last_seen_icon.setImageResource(R.drawable.ic_green_single);
                                    is_online = true;
                                    break;

                                case 1:
                                    if (diff.intValue() < 30 && diff != null){
                                        last_seen.setText("Idle");
                                        last_seen_icon.setImageResource(R.drawable.ic_single_orange);
                                        is_online = false;
                                    } else {
                                        last_seen.setText("Away");
                                        last_seen_icon.setImageResource(R.drawable.ic_red_single);
                                        is_online = false;
                                    }
                                    break;

                                case 2:
                                    last_seen.setText("Night");
                                    last_seen_icon.setImageResource(R.drawable.ic_moon);
                                    is_online = false;
                                    break;

                                case 3:
                                    last_seen.setText("");
                                    last_seen_icon.setImageResource(R.drawable.ic_invisible);
                                    is_online = false;
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            Log.d("listening last seen", "Current data: null");
                        }
                    }
                });
    }

    private void setup_pp() {
        ppicture = findViewById(R.id.chat_pp);
        if (!pp_url.equals("")) {
            Glide
                    .with(ChatPage.this)
                    .load(pp_url)
                    .centerCrop()
                    .into(ppicture);
        }
    }

    private void setup_fire_recycler() {
        Query query = messages_reference.orderBy("time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatFireMessages> options = new FirestoreRecyclerOptions.Builder<ChatFireMessages>()
                .setQuery(query, ChatFireMessages.class)
                .build();


        recyclerView = findViewById(R.id.messages_recycler);
        adapter = new ChatFireAdapter(options, this, ownData.getName(), ChatPage.this, recyclerView, db, username);

        recyclerView.setAdapter(adapter);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatPage.this));
        //recyclerView.scrollToPosition(adapter.getItemCount()-1);

    }

    private void setup_toolbar() {
        back_button = findViewById(R.id.button_back_chat);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setup_fab() {

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals("")) {
                    ContainerMethods.send_message(db, username, pp_url, message.getText().toString());
                    if (!is_online){
                        ContainerMethods.get_notId_for_send_message(db, username, message.getText().toString());
                    }
                    message.setText("");
                }
            }
        });
    }

    private void getExtras() {
        username_text = findViewById(R.id.text1);

        Intent get = getIntent();
        Bundle bundle = get.getExtras();

        if (bundle != null) {
            username = bundle.getString("username");
            pp_url = bundle.getString("pp_url");
        }
        if (username != null) {
            username_text.setText(username);
        }
    }

    @Override
    public void OnRecyclerClickListener(String username, String status, String pp_url) {
        super.OnRecyclerClickListener(username, status, pp_url);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        still_running_chat = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        still_running_chat = false;
        ContainerMethods.set_last_seen_if(db,0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!still_running_chat){
            ContainerMethods.set_last_seen_if(db,1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if (!still_running_chat){
            ContainerMethods.set_last_seen_if(db,1);
        }
    }

}