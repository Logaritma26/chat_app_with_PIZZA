package com.example.chat_app.fragments.ui.home_page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.click_manager.AbstractMakeClickFragment;
import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.chat_page.ChatPage;
import com.example.chat_app.click_manager.DeleteButtonVisibleFragments;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class fragment_main extends AbstractMakeClickFragment {
    private static final String TAG = "Fragment Main";

    public FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference chats_home;
    FirebaseFirestore db;

    OwnData ownData;

    public static RecyclerView recyclerView;
    public static HomeFireAdapter adapter;

    private String username_shared;


    /*List<String> message = new ArrayList<>();
    List<String> pp_url = new ArrayList<>();*/

    public static List<Integer> selected_adapter_position = new ArrayList<>();
    public static List<String> selected_username = new ArrayList<>();
    public static boolean is_long_clicked = false;
    private int selected_view_count = 0;
    DeleteButtonVisibleFragments deleteButtonVisibleFragments;

    public fragment_main(DeleteButtonVisibleFragments deleteButtonVisibleFragments) {
        this.deleteButtonVisibleFragments = deleteButtonVisibleFragments;
    }

    private Context context;
    private Activity activity_home;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.context = context;
            activity_home = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ownData = OwnData.getInstance();



        chats_home = firebaseFirestore.collection("user_val")
                .document(ownData.getName() + "@pizza.com").collection("chats");

        db = FirebaseFirestore.getInstance();


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.chats_home_page_recycler);
        setup_fire_recycler();

    }


    private void setup_fire_recycler() {
        Query query = chats_home.orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<HomeFireChats> options = new FirestoreRecyclerOptions.Builder<HomeFireChats>()
                .setQuery(query, HomeFireChats.class)
                .build();


        adapter = new HomeFireAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void OnRecyclerClickListener(String username, String pp_url, View view, int position) {

        if (!is_long_clicked){
            Toast.makeText(activity_home, "deneme calisiyor position : " + (username) , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity_home, ChatPage.class);
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



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public boolean onBackPressed() {
        if (selected_view_count == 0) {
            return false;
        } else {
            for (int a = 0; a < adapter.getItemCount(); a++){
                View itemView = recyclerView.getLayoutManager().findViewByPosition(a);
                View cover = itemView.findViewById(R.id.view_cover);
                cover.setVisibility(View.GONE);
            }
            is_long_clicked = false;
            return true;
        }

    }


}
