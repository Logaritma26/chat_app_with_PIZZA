package com.example.chat_app.fragments.ui.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.Recycler_Click;
import com.example.chat_app.fragments.fragments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_add_contact extends Fragment implements Recycler_Click {
    private static final String TAG = "Fragment Add Contact";

    RecyclerView recyclerView;
    Recycler_Click recycler_click;

    SearchDataHolder dataHolder;
    public static SearchAdapter adapter;






    private Context context;
    private Activity activity_search;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.context = context;
            activity_search = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_search);
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity_search));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        dataHolder = SearchDataHolder.getInstance();


    }


    @Override
    public void OnContactAddClickListener(int position) {
        String name = dataHolder.getUsername().get(position);

        Toast.makeText(activity_search, name, Toast.LENGTH_SHORT).show();
    }

}
