package com.example.chat_app.chat_page;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.AbstractMakeClickAppCompat;
import com.example.chat_app.click_manager.Recycler_Click;
import com.example.chat_app.login.MainActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ChatPage extends AbstractMakeClickAppCompat implements Recycler_Click {


    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference messages_reference;
    FirebaseFirestore db;

    OwnData ownData;

    private String username = null;
    private String pp_url = null;

    TextView username_text;
    ImageView ppicture;
    FloatingActionButton fab;

    EditText message;
    Button back_button;

    RecyclerView recyclerView;
    ChatFireAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        Window window = ChatPage.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChatPage.this, R.color.grif7));


        getExtras();

        setup_pp();


        ownData = OwnData.getInstance();
        messages_reference = firebaseFirestore.collection("user_val")
                .document(ownData.getName()+"@pizza.com").collection("chats")
                .document(username+"@pizza.com").collection(username+"@pizza.com");

        db = FirebaseFirestore.getInstance();

        setup_fab();
        setup_toolbar();
        setup_fire_recycler();



        message = findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        });

    }

    private void setup_pp() {
        ppicture = findViewById(R.id.chat_pp);
        if (!pp_url.equals("")){
            Picasso.get().load(pp_url).into(ppicture);
        }
    }

    private void setup_fire_recycler(){
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
                if (!message.getText().toString().equals("")){
                    ContainerMethods.send_message(db, username, pp_url, message.getText().toString());
                    message.setText("");

                }
            }
        });
    }

    private void getExtras() {
        username_text = findViewById(R.id.text1);

        Intent get = getIntent();
        Bundle bundle = get.getExtras();

        if(bundle != null){
            username = bundle.getString("username");
            pp_url = bundle.getString("pp_url");
        }
        if (username != null){
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}