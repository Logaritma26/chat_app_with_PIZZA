package com.example.chat_app.fragments.ui.contact_list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.AbstractMakeClickAppCompat;
import com.example.chat_app.chat_page.ChatPage;
import com.example.chat_app.click_manager.AbstractMakeClickFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FireContactList extends AbstractMakeClickFragment {

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference friends_reference;

    OwnData ownData;

    RecyclerView recyclerView;
    FireAdapter_Contact adapter;

    private Context context;
    private Activity activity_contact;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.context = context;
            activity_contact = (Activity) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fire_contact_list, container, false);

        ownData = OwnData.getInstance();
        friends_reference = firebaseFirestore.collection("user_val")
                .document(ownData.getName()+"@pizza.com").collection("friends");

        recyclerView = view.findViewById(R.id.contact_list_recycler);

        setup_fire_recycler();


        return view;
    }


    private void setup_fire_recycler(){
        Query query = friends_reference.orderBy("username", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ContactInformation> options = new FirestoreRecyclerOptions.Builder<ContactInformation>()
                .setQuery(query, ContactInformation.class)
                .build();

        adapter = new FireAdapter_Contact(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity_contact));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void OnRecyclerClickListener(String username, String pp_url) {
        //Toast.makeText(this, "deneme calisiyor position : " + (username) , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(activity_contact, ChatPage.class);
        intent.putExtra("username", username);
        intent.putExtra("pp_url", pp_url);
        startActivity(intent);
    }

}