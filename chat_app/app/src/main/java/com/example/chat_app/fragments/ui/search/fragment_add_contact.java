package com.example.chat_app.fragments.ui.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.OwnData;
import com.example.chat_app.R;
import com.example.chat_app.click_manager.AbstractMakeClickFragment;
import com.example.chat_app.click_manager.Recycler_Click;
import com.example.chat_app.fragments.ui.pending.PendingDataHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

public class fragment_add_contact extends AbstractMakeClickFragment {
    private static final String TAG = "Fragment Add Contact";

    RecyclerView recyclerView;
    public static SearchAdapter adapter;

    SearchDataHolder searchDataHolder;
    OwnData ownData;
    PendingDataHolder pendingDataHolder;

    FirebaseAuth fAuth;
    FirebaseFirestore db;

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


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        searchDataHolder = SearchDataHolder.getInstance();
        ownData = OwnData.getInstance();
        pendingDataHolder = PendingDataHolder.getInstance();


        OneSignal.startInit(activity_search)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }


    @Override
    public void OnRecyclerClickListener(int position) {

        // name = istek gonderilen kullanici
        String name = searchDataHolder.getUsername().get(position);
        name += "@pizza.com";

        ContainerMethods.send_request(db, name, position, activity_search);

    }



}
