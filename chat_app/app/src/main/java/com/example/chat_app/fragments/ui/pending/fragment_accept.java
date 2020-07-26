package com.example.chat_app.fragments.ui.pending;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.AbstractMakeClickFragment;
import com.example.chat_app.click_manager.Recycler_Click;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class fragment_accept extends AbstractMakeClickFragment {
    private static final String TAG = "Fragment Accept";

    SearchDataHolder dataHolder;
    PendingDataHolder pendingDataHolder;

    RecyclerView recyclerView;
    public static PendingAdapter adapter;


    FirebaseAuth fAuth;
    FirebaseFirestore db;



    private Context context;
    private Activity activity_accept;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            this.context = context;
            activity_accept = (Activity) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accept, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //dataHolder = SearchDataHolder.getInstance();
        pendingDataHolder = PendingDataHolder.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_pending);
        adapter = new PendingAdapter(this, activity_accept);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity_accept));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);


    }


    @Override
    public void OnRecyclerClickListener(int position) {
        Toast.makeText(activity_accept, "accept", Toast.LENGTH_SHORT).show();
        ContainerMethods.accept_request(position, db);
        pendingDataHolder.clean_at(position);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void OnDeclineClickListener(int position) {
        Toast.makeText(activity_accept, "decline", Toast.LENGTH_SHORT).show();
        ContainerMethods.delete_request(db, position);
        pendingDataHolder.clean_at(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        ContainerMethods.get_requests(db);

    }




}
