package com.example.chat_app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.click_manager.DeleteButtonVisibleFragments;
import com.example.chat_app.click_manager.IOBackPressed;
import com.example.chat_app.OwnData;
import com.example.chat_app.fragments.ui.contact_list.FireContactList;
import com.example.chat_app.fragments.ui.pending.fragment_accept;
import com.example.chat_app.login.MainActivity;
import com.example.chat_app.R;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.example.chat_app.fragments.ui.home_page.fragment_main;
import com.example.chat_app.menu.profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class fragments extends AppCompatActivity implements DeleteButtonVisibleFragments {

    public static ViewPager mViewPager;
    SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    SearchDataHolder searchDataHolder;
    OwnData ownData;

    public SearchView searchView;
    TabLayout tabs;
    Toolbar toolbar;
    Button delete_button;
    ImageView pp_home;

    int last_key_lenght = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        searchDataHolder = SearchDataHolder.getInstance();
        ownData = OwnData.getInstance();

        SharedPreferences sharedPref = fragments.this.getSharedPreferences("Username", Context.MODE_PRIVATE);
        String username_shared = sharedPref.getString("username", "none");
        ownData.setName(username_shared);

        set_up_elements_ui();

        search_methods(searchView);


    }


    private void set_up_elements_ui() {
        pp_home = findViewById(R.id.image_pp_home);
        pp_home.setImageResource(R.drawable.cat_pp);
        pp_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1 = new Intent(fragments.this, profile.class);
                startActivity(intent_1);
                /*fAuth.signOut();
                SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
                sharedPref.edit().remove("username").apply();
                sharedPref.edit().clear().apply();
                //delete_player_id -> update notification (delete related notification id and go into opening page with intent)
                delete_player_id(ownData.getName());*/
            }
        });
        tabs = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        delete_button = findViewById(R.id.button_delete);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment_main.is_long_clicked) {
                    for (int i = 0; i < fragment_main.selected_adapter_position.size(); i++) {
                        try {
                            fragment_main.recyclerView.findViewHolderForAdapterPosition(fragment_main.selected_adapter_position.get(i)).itemView.findViewById(R.id.view_cover).setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }
                    }

                    ContainerMethods.delete_chat(db, fragment_main.selected_username);
                    fragment_main.is_long_clicked = false;
                    delete_button.setVisibility(View.GONE);
                }
            }
        });

        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);


    }

    private void search_methods(final SearchView searchView) {

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabs.setVisibility(View.GONE);
                if (mViewPager.getCurrentItem() == 0) {
                    searchView.setQueryHint("Some other things");
                } else if (mViewPager.getCurrentItem() == 2) {
                    searchView.setQueryHint("Search for new people ...");
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (mViewPager.getCurrentItem() == 2) {
                    if (last_key_lenght == 0) {
                        last_key_lenght = newText.length();
                    } else if (newText.length() < 3) {
                        searchDataHolder.clean();
                        last_key_lenght = newText.length();
                    } else if (last_key_lenght > newText.length()) {
                        searchDataHolder.clean();
                        ContainerMethods.find_user(newText, db, ownData.getName()); /////////// could bug -> works
                        last_key_lenght = newText.length();
                    } else {
                        searchDataHolder.clean();
                        ContainerMethods.find_user(newText, db, ownData.getName());  /////////// could bug -> works
                        last_key_lenght = newText.length();
                    }
                }

                return false;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchDataHolder.clean();
                tabs.setVisibility(View.VISIBLE);
                return false;
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new fragment_main(this), "Chats");
        adapter.addFragment(new FireContactList(), "Contacts");
        adapter.addFragment(new fragment_add_contact(), "Search");
        adapter.addFragment(new fragment_accept(), "Pending");
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item1:
                Intent intent_1 = new Intent(fragments.this, profile.class);
                startActivity(intent_1);
                return true;

            case R.id.item2:
                fAuth.signOut();
                SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
                sharedPref.edit().remove("username").apply();
                sharedPref.edit().clear().apply();
                //delete_player_id -> update notification (delete related notification id and go into opening page with intent)
                delete_player_id(ownData.getName());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void delete_player_id(String own_username) {
        own_username += "@pizza.com";

        final DocumentReference reference_val = db.collection("user_val").document(own_username);
        final DocumentReference reference_nicks = db.collection("user_nicks").document(own_username);

        update_field_notification_id(reference_val, "notification_id", "none", reference_nicks, "notification", "none");
    }

    private void update_field_notification_id(DocumentReference reference1, String field_name1, String value1, DocumentReference reference2, String field_name2, String value2) {

        reference1
                .update(field_name1, value1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("renew player id", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("renew player id", "Error updating document", e);
                    }
                });

        reference2
                .update(field_name2, value2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("renew player id", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("renew player id", "Error updating document", e);
                    }
                });


        Intent intent_2 = new Intent(fragments.this, MainActivity.class);
        finish();
        startActivity(intent_2);
    }


    @Override
    public void onBackPressed() {

        Fragment fragment = adapter.getItem(0);

        if (!(fragment instanceof IOBackPressed) || !((IOBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }

        //super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void DeleteVisible() {
        delete_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void DeleteGone() {
        delete_button.setVisibility(View.GONE);
    }
}