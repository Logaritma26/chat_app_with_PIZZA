package com.example.chat_app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import com.example.chat_app.fragments.fragments;
import com.example.chat_app.fragments.ui.pending.PendingAdapter;
import com.example.chat_app.fragments.ui.pending.PendingDataHolder;
import com.example.chat_app.fragments.ui.pending.fragment_accept;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerMethods {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    static SearchDataHolder dataHolder = SearchDataHolder.getInstance();
    static PendingDataHolder pendingDataHolder = PendingDataHolder.getInstance();

    public ContainerMethods() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dataHolder = SearchDataHolder.getInstance();
        pendingDataHolder = PendingDataHolder.getInstance();

    }


    public static void send_notification(int position, Context context){
        String own_name = dataHolder.getName();
        String message = own_name + " wants to add you to the contact list.";

        if (dataHolder.getNotification_url().get(position).equals("none")){
            Toast.makeText(context, "The user is not active but request has sent.", Toast.LENGTH_SHORT).show();
        }else {
            try {
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + dataHolder.getNotification_url().get(position) + "']}"), null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        fragments.searchView.setIconified(true);
        dataHolder.clean();
        fragment_add_contact.adapter.notifyDataSetChanged();

    }

    public static void get_requests(final FirebaseFirestore db) {
        db.collection("user_val").document(dataHolder.getName() + "@pizza.com")
                .collection("requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("snap listener pending", "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    pendingDataHolder.clean();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        if (doc.get("username") != null) {
                            String username = (String) doc.get("username");
                            get_request_info(username, db);
                        }
                    }
                }
            }
        });

    }

    public static void get_request_info(String username, FirebaseFirestore db) {
        db.collection("user_nicks").document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            String name_holder = null;
                            String status_holder = null;
                            String pic_holder = null;
                            String notification = null;
                            Boolean status_permissions_holder = null;
                            Boolean pic_permissions_holder = null;

                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("get info for request", "DocumentSnapshot data: " + document.getData());

                                try {
                                    name_holder = (String) document.get("username");
                                    status_holder = (String) document.get("status");
                                    pic_holder = (String) document.get("pp");
                                    notification = (String) document.get("notification");
                                    status_permissions_holder = (Boolean) document.get("status_visibilty");
                                    pic_permissions_holder = (Boolean) document.get("pp_visibilty");

                                } catch (Exception e) {
                                    Log.d("data null search", e.getMessage());
                                }


                                if (name_holder != null) {
                                    if (!check_if_have_equal(name_holder)) {
                                        pendingDataHolder.addUsername(name_holder);
                                        pendingDataHolder.addStatus(status_holder);
                                        pendingDataHolder.addPic_url(pic_holder);
                                        pendingDataHolder.addNotification(notification);
                                        pendingDataHolder.addStatus_permissions(status_permissions_holder);
                                        pendingDataHolder.addPic_permissions(pic_permissions_holder);
                                        fragment_accept.adapter.notifyDataSetChanged();
                                    }
                                }

                            } else {
                                Log.d("get info for request", "No such document");
                            }
                        } else {
                            Log.d("get info for request", "get failed with ", task.getException());
                        }
                    }
                });

    }

    public static void send_request(FirebaseFirestore db, String name, final int position, final Context context) {

        Map<String, Object> request = new HashMap<>();
        request.put("username", dataHolder.getName() + "@pizza.com");

        db.collection("user_val").document(name)
                .collection("requests").document(dataHolder.getName())
                .set(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("sending request", "requests sent");
                        send_notification(position, context);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("sending request", "onFailure: " + e.getMessage());
            }
        });


        Map<String, Object> request_2 = new HashMap<>();
        request_2.put("username", name);

        db.collection("user_val").document(dataHolder.getName() + "@pizza.com")
                .collection("requests sent").document(name)
                .set(request_2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("requests sent", "requests sended");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("requests sent", "onFailure: " + e.getMessage());
            }
        });

    }

    public static void delete_request(FirebaseFirestore db, int position){


        db.collection("user_val").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .collection("requests sent").document(dataHolder.getName() + "@pizza.com")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleting request", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deleting request", "Error deleting document", e);
                    }
                });

        db.collection("user_val").document(dataHolder.getName() + "@pizza.com")
                .collection("requests").document(pendingDataHolder.getUsername().get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleting request", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deleting request", "Error deleting document", e);
                    }
                });


    }

    public static void find_user(final String item, FirebaseFirestore db, final String self_username) {

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
                                String not_url = null;
                                Boolean status_permissions_holder = null;
                                Boolean pic_permissions_holder = null;

                                try {
                                    name_holder = (String) document.get("username");
                                    status_holder = (String) document.get("status");
                                    pic_holder = (String) document.get("pp");
                                    not_url = (String) document.get("notification");
                                    status_permissions_holder = (Boolean) document.get("status_visibilty");
                                    pic_permissions_holder = (Boolean) document.get("pp_visibilty");

                                } catch (Exception e) {
                                    Log.d("data null search", e.getMessage());
                                }

                                if (name_holder != null && !name_holder.equals(self_username)) {
                                    if (name_holder.contains(item)) {
                                        if (!check_if_have_equal(name_holder)) {
                                            dataHolder.addUsername(name_holder);
                                            dataHolder.addStatus(status_holder);
                                            dataHolder.addPic_url(pic_holder);
                                            dataHolder.addNotification_url(not_url);
                                            dataHolder.addStatus_permissions(status_permissions_holder);
                                            dataHolder.addPic_permissions(pic_permissions_holder);
                                            fragment_add_contact.adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                            }

                        } else {
                            Log.d("get data for search", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private static boolean check_if_have_equal(String name) {
        List<String> temp_list = dataHolder.getUsername();

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).equals(name)) {
                return true;
            }
        }

        return false;
    }

    ;

    public static void get_own_username(FirebaseFirestore db, FirebaseAuth fAuth) {

        db.collection("user_nicks").document(fAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Log.d("document exists?", "DocumentSnapshot data: " + document.getData());

                                String name_holder = null;

                                try {
                                    name_holder = (String) document.get("username");

                                } catch (Exception e) {
                                    Log.d("data null search", e.getMessage());
                                }

                                if (name_holder != null) {
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
