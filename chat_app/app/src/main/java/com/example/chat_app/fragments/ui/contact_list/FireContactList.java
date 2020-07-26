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
import com.example.chat_app.click_manager.DeleteButtonVisibleFragments;
import com.example.chat_app.fragments.fragments;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FireContactList extends AbstractMakeClickFragment {

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference friends_reference;

    OwnData ownData;

    public static RecyclerView recyclerView;
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


    public static List<Integer> selected_adapter_position = new ArrayList<>();
    public static List<String> selected_username = new ArrayList<>();
    public static boolean is_long_clicked = false;
    private int selected_view_count = 0;
    DeleteButtonVisibleFragments deleteButtonVisibleFragments;


    public FireContactList(DeleteButtonVisibleFragments deleteButtonVisibleFragments) {
        this.deleteButtonVisibleFragments = deleteButtonVisibleFragments;
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

        adapter = new FireAdapter_Contact(options, this, activity_contact);

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
    public void OnRecyclerClickListener(String username, String pp_url, View view, int position) {

        if (!is_long_clicked){
            fragments.still_running = true;
            Intent intent = new Intent(activity_contact, ChatPage.class);
            intent.putExtra("username", username);
            intent.putExtra("pp_url", pp_url);
            startActivity(intent);
        } else {
            longClick(view, position, username);
        }

    }

    @Override
    public void OnRecyclerClickListener(View view, int position, TextView username) {
        if (!is_long_clicked){
            is_long_clicked = true;
            deleteButtonVisibleFragments.DeleteVisible();
            longClick(view, position, username);
        } else {

        }
    }

    private void longClick(View view, int position, TextView username){
        if (view.getVisibility() == View.VISIBLE){
            selected_view_count--;
            find_remove_username(username.getText().toString());
            find_remove_position(position);
            view.setVisibility(View.GONE);
            if (selected_view_count == 0) {
                deleteButtonVisibleFragments.DeleteGone();
                is_long_clicked = false;
            }
        } else {
            selected_view_count++;
            selected_adapter_position.add(position);
            view.setVisibility(View.VISIBLE);
            selected_username.add(username.getText().toString());
        }

    }

    private void longClick(View view, int position, String username){
        if (view.getVisibility() == View.VISIBLE){
            selected_view_count--;
            find_remove_username(username);
            find_remove_position(position);
            view.setVisibility(View.GONE);
            if (selected_view_count == 0) {
                deleteButtonVisibleFragments.DeleteGone();
                is_long_clicked = false;
            }
        } else {
            selected_view_count++;
            selected_adapter_position.add(position);
            view.setVisibility(View.VISIBLE);
            selected_username.add(username);
        }


    }


    private void find_remove_username(String username){
        for (int i = 0; i < selected_username.size(); i++) {
            if (selected_username.get(i).equals(username)){
                selected_username.remove(i);
            }
        }
    };

    private void find_remove_position(int position){
        for (int i = 0; i < selected_adapter_position.size(); i++) {
            if (selected_adapter_position.get(i) == position){
                selected_adapter_position.remove(i);
            }
        }
    };


}