package com.example.chat_app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ContainerMethods {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    static SearchDataHolder dataHolder = SearchDataHolder.getInstance();

    public ContainerMethods() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dataHolder = SearchDataHolder.getInstance();
    }





    public static void find_user(final String item, FirebaseFirestore db, final String self_username){

        db.collection("user_nicks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("get data for search", "Success");

                                String name_holder = null;
                                String status_holder = null;
                                String pic_holder = null;
                                Boolean status_permissions_holder = null;
                                Boolean pic_permissions_holder = null;

                                try {
                                    name_holder = (String) document.get("username");
                                    status_holder = (String) document.get("status");
                                    pic_holder = (String) document.get("pp");
                                    status_permissions_holder = (Boolean) document.get("status_visibilty");
                                    pic_permissions_holder = (Boolean) document.get("pp_visibilty");

                                }catch (Exception e){
                                    Log.d("data null search", e.getMessage());
                                }

                                if (name_holder != null && !name_holder.equals(self_username)){
                                    if (name_holder.contains(item)){
                                        dataHolder.addUsername(name_holder);
                                        dataHolder.addStatus(status_holder);
                                        dataHolder.addPic_url(pic_holder);
                                        dataHolder.addStatus_permissions(status_permissions_holder);
                                        dataHolder.addPic_permissions(pic_permissions_holder);
                                        fragment_add_contact.adapter.notifyDataSetChanged();
                                    }
                                }

                            }

                        } else {
                            Log.d("get data for search", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    public static void get_own_username(FirebaseFirestore db, FirebaseAuth fAuth){

        db.collection("user_nicks").document(fAuth.getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Log.d("document exists?", "DocumentSnapshot data: " + document.getData());

                        String name_holder = null;

                        try {
                            name_holder = (String) document.get("username");
                        }catch (Exception e){
                            Log.d("data null search", e.getMessage());
                        }

                        if (name_holder != null){
                            dataHolder.setName(name_holder);
                        }

                    } else {
                        Log.d("document exists?", "No such document");
                    }
                } else {
                    Log.d("get data for search", "Error getting documents: ", task.getException());
                }
            }
        });

    }


}
