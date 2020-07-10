package com.example.chat_app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.chat_app.fragments.ui.pending.PendingDataHolder;
import com.example.chat_app.fragments.ui.pending.fragment_accept;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContainerMethods {

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    static SearchDataHolder searchDataHolder = SearchDataHolder.getInstance();
    static PendingDataHolder pendingDataHolder = PendingDataHolder.getInstance();
    static OwnData ownData = OwnData.getInstance();

    public ContainerMethods() {
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        searchDataHolder = SearchDataHolder.getInstance();
        pendingDataHolder = PendingDataHolder.getInstance();
        ownData = OwnData.getInstance();

    }

    public static void delete_chat(FirebaseFirestore db, List<String> username) {

        for (int i = 0; i < username.size(); i++) {

            db.collection("user_val")
                    .document(ownData.getName() + "@pizza.com").collection("chats")
                    .document(username.get(i) + "@pizza.com").collection(username.get(i) + "@pizza.com")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                    Log.d("get for delete", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d("get for delete", "Error getting documents: ", task.getException());
                            }
                        }
                    });

            db.collection("user_val")
                    .document(ownData.getName() + "@pizza.com").collection("chats")
                    .document(username.get(i) + "@pizza.com").delete();

        }

    }

    public static void send_message(FirebaseFirestore db, String username, String pp_url, String message) {

        Map<String, Object> data_for_own = new HashMap<>();
        data_for_own.put("message", message);
        data_for_own.put("username", ownData.getName());
        data_for_own.put("time", Timestamp.now());
        data_for_own.put("seen", false);

        String uniqueID = UUID.randomUUID().toString();

        db.collection("user_val").document(ownData.getName() + "@pizza.com")
                .collection("chats").document(username + "@pizza.com")
                .collection(username + "@pizza.com").document(uniqueID)
                .set(data_for_own)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saving message for own", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saving message for own", "Error writing document", e);
                    }
                });

        Map<String, Object> data_own = new HashMap<>();
        data_own.put("username", username);
        data_own.put("message", message);
        data_own.put("pp_url", pp_url);
        data_own.put("time", Timestamp.now());

        db.collection("user_val").document(ownData.getName() + "@pizza.com")
                .collection("chats").document(username + "@pizza.com")
                .set(data_own)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saving own data", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saving own data", "Error writing document", e);
                    }
                });


        Map<String, Object> data_for_other = new HashMap<>();
        data_for_other.put("message", message);
        data_for_other.put("username", ownData.getName());
        data_for_other.put("time", Timestamp.now());
        data_for_other.put("seen", false);

        db.collection("user_val").document(username + "@pizza.com")
                .collection("chats").document(ownData.getName() + "@pizza.com")
                .collection(ownData.getName() + "@pizza.com").document(uniqueID)
                .set(data_for_other)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saving message for own", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saving message for own", "Error writing document", e);
                    }
                });


        Map<String, Object> data_other = new HashMap<>();
        data_other.put("username", ownData.getName());
        data_other.put("message", message);
        data_other.put("pp_url", ownData.getPic_url());
        data_other.put("time", Timestamp.now());

        db.collection("user_val").document(username + "@pizza.com")
                .collection("chats").document(ownData.getName() + "@pizza.com")
                .set(data_other)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("saving message for own", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("saving message for own", "Error writing document", e);
                    }
                });


    }

    public static void message_seen(final FirebaseFirestore db, final String username){

        db.collection("user_val").document(username + "@pizza.com")
                .collection("chats").document(ownData.getName() + "@pizza.com")
                .collection(ownData.getName() + "@pizza.com")
                .whereEqualTo("seen", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("message seen", document.getId() + " => " + document.getData());
                                document.getReference().update("seen", true);
                                //seen_2(db, username, document.getId());
                            }
                        } else {
                            Log.d("message seen", "Error getting documents: ", task.getException());
                        }
                    }
                });




        /*db.collection("user_val").document(username + "@pizza.com")
                .collection("chats").document(ownData.getName() + "@pizza.com")
                .collection(ownData.getName()).document()
                .update("seen", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("message seen", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("message seen", "Error writing document", e);
                    }
                });*/

        //b787a1bf-a069-43b8-a99e-6afaab525b9c
    }
    public static void seen_2(FirebaseFirestore db, String username, String doc_id){

        db.collection("user_val").document(username + "@pizza.com")
                .collection("chats").document(ownData.getName() + "@pizza.com")
                .collection(ownData.getName() + "@pizza.com").document(doc_id)
                .update("seen", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("message seen", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("message seen", "Error writing document", e);
                    }
                });
    }

    public static void accept_request(int position, FirebaseFirestore db) {

        ownData.addFriendList(pendingDataHolder.getUsername().get(position));

        Map<String, Object> friend_data = new HashMap<>();
        friend_data.put("username", pendingDataHolder.getUsername().get(position));
        friend_data.put("pp", pendingDataHolder.getPic_url().get(position));
        friend_data.put("pp_visibilty", pendingDataHolder.getPic_permissions().get(position));
        friend_data.put("status", pendingDataHolder.getStatus().get(position));
        friend_data.put("status_visibilty", pendingDataHolder.getStatus_permissions().get(position));
        friend_data.put("notification", pendingDataHolder.getNotification().get(position));


        db.collection("user_val").document(ownData.getName() + "@pizza.com")
                .collection("friends").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .set(friend_data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.w("add friend", "adding friend success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add friend", "Error adding document", e);
                    }
                });


        db.collection("user_val").document(ownData.getName() + "@pizza.com")
                .collection("requests").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("delete from list", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("delete from list", "Error deleting document", e);
                    }
                });


        Map<String, Object> my_data = new HashMap<>();
        my_data.put("username", ownData.getName());
        my_data.put("pp", ownData.getPic_url());
        my_data.put("pp_visibilty", ownData.getPic_permission());
        my_data.put("status", ownData.getStatus());
        my_data.put("status_visibilty", ownData.getStatus_permission());
        my_data.put("notification", ownData.getNotification_ch());


        db.collection("user_val").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .collection("friends").document(ownData.getName() + "@pizza.com")
                .set(my_data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.w("add friend", "adding friend success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("add friend", "Error adding document", e);
                    }
                });

        db.collection("user_val").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .collection("requests sent").document(ownData.getName() + "@pizza.com")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("delete from list", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("delete from list", "Error deleting document", e);
                    }
                });

    }

    public static void send_notification(int position, Context context) {
        String own_name = ownData.getName();
        String message = own_name + " wants to add you to the contact list.";

        Boolean check = searchDataHolder.getNotification_url().get(position).equals("none");

        if (check != null) {
            if (check) {
                Toast.makeText(context, "The user is not active but request has sent.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + message + "'}, 'include_player_ids': ['" + searchDataHolder.getNotification_url().get(position) + "']}"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        ownData.addRequests_sent_list(searchDataHolder.getUsername().get(position));
        searchDataHolder.clean();
        fragment_add_contact.adapter.notifyDataSetChanged();

    }

    public static void get_requests(final FirebaseFirestore db) {
        db.collection("user_val").document(ownData.getName() + "@pizza.com")
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
        db.collection("user_nicks").document(username + "@pizza.com")
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
        request.put("username", searchDataHolder.getName());

        db.collection("user_val").document(name)
                .collection("requests").document(searchDataHolder.getName() + "@pizza.com")
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

        String username = name;
        username = username.replace("@pizza.com", "");

        Map<String, Object> request_2 = new HashMap<>();
        request_2.put("username", username);


        db.collection("user_val").document(searchDataHolder.getName() + "@pizza.com")
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

    public static void delete_request(FirebaseFirestore db, int position) {


        db.collection("user_val").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
                .collection("requests sent").document(ownData.getName() + "@pizza.com")
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

        db.collection("user_val").document(ownData.getName() + "@pizza.com")
                .collection("requests").document(pendingDataHolder.getUsername().get(position) + "@pizza.com")
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
                                            if (!check_if_have_friend(name_holder)) {
                                                if (!check_if_have_request(name_holder)) {
                                                    searchDataHolder.addUsername(name_holder);
                                                    searchDataHolder.addStatus(status_holder);
                                                    searchDataHolder.addPic_url(pic_holder);
                                                    searchDataHolder.addNotification_url(not_url);
                                                    searchDataHolder.addStatus_permissions(status_permissions_holder);
                                                    searchDataHolder.addPic_permissions(pic_permissions_holder);
                                                    fragment_add_contact.adapter.notifyDataSetChanged();
                                                }
                                            }
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
        List<String> temp_list = searchDataHolder.getUsername();

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static boolean check_if_have_friend(String name) {
        List<String> temp_list = ownData.getFriendList();

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static boolean check_if_have_request(String name) {
        List<String> temp_list = ownData.getRequests_sent_list();

        for (int i = 0; i < temp_list.size(); i++) {
            if (temp_list.get(i).equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static void get_own_data(FirebaseFirestore db, FirebaseAuth fAuth) {

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
                                String pic_url = null;
                                Boolean pic_permission = null;
                                String status = null;
                                Boolean status_permission = null;
                                String notification_ch = null;

                                try {
                                    name_holder = (String) document.get("username");
                                    pic_url = (String) document.get("pp");
                                    pic_permission = (Boolean) document.get("pp_visibilty");
                                    status = (String) document.get("status");
                                    status_permission = (Boolean) document.get("status_visibilty");
                                    notification_ch = (String) document.get("notification");

                                } catch (Exception e) {
                                    Log.d("data null search", e.getMessage());
                                }

                                if (name_holder != null) {
                                    searchDataHolder.setName(name_holder);
                                    ownData.setName(name_holder);
                                    ownData.setPic_url(pic_url);
                                    ownData.setPic_permission(pic_permission);
                                    ownData.setStatus(status);
                                    ownData.setStatus_permission(status_permission);
                                    ownData.setNotification_ch(notification_ch);
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

    public static void get_own_friends(FirebaseFirestore db, FirebaseAuth fAuth) {

        db.collection("user_val").document(fAuth.getCurrentUser().getEmail())
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("get friends list", document.getId() + " => " + document.getData());
                                ownData.addFriendList(document.getString("username"));
                            }
                        } else {
                            Log.d("get friends list", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void get_own_sent_requests(FirebaseFirestore db, FirebaseAuth fAuth) {

        db.collection("user_val").document(fAuth.getCurrentUser().getEmail())
                .collection("requests sent")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("get friends list", document.getId() + " => " + document.getData());
                                ownData.addRequests_sent_list(document.getString("username"));
                            }
                        } else {
                            Log.d("get friends list", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
