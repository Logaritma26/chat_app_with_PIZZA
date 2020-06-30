package com.example.chat_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.example.chat_app.ContainerMethods;
import com.example.chat_app.fragments.ui.pending.PendingDataHolder;
import com.example.chat_app.fragments.ui.pending.fragment_accept;
import com.example.chat_app.login.MainActivity;
import com.example.chat_app.R;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.example.chat_app.fragments.ui.fragment_main;
import com.example.chat_app.menu.profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class fragments extends AppCompatActivity {

    private ViewPager mViewPager;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    SearchDataHolder dataHolder;
    PendingDataHolder pendingDataHolder;

    public static SearchView searchView;
    TabLayout tabs;
    TextView title;
    Toolbar toolbar;

    int last_key_lenght = 0;
    String own_username = "";


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

        dataHolder = SearchDataHolder.getInstance();
        //pendingDataHolder = PendingDataHolder.getInstance();

        own_username = dataHolder.getName();



        set_up_elements_ui();



        fab_set();

        search_methods(searchView);

    }

    private void set_up_elements_ui() {
        tabs = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);


        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);
        tabs.setupWithViewPager(mViewPager);

    }

    private void fab_set() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    private void search_methods(SearchView searchView) {
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabs.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                own_username = dataHolder.getName();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (mViewPager.getCurrentItem() == 1) {
                    if (last_key_lenght == 0){
                        last_key_lenght = newText.length();
                    } else if (newText.length() < 3) {
                        dataHolder.clean();
                        last_key_lenght = newText.length();
                    } else if (last_key_lenght > newText.length()) {
                        dataHolder.clean();
                        ContainerMethods.find_user(newText, db, own_username);
                        last_key_lenght = newText.length();
                    } else {
                        dataHolder.clean();
                        ContainerMethods.find_user(newText, db, own_username);
                        last_key_lenght = newText.length();
                    }
                }

                return false;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                dataHolder.clean();
                tabs.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new fragment_main(), "CHATS");
        adapter.addFragment(new fragment_add_contact(), "SEARCH");
        adapter.addFragment(new fragment_accept(), "PENDING");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.item1:
                Intent intent_1 = new Intent(fragments.this, profile.class);
                startActivity(intent_1);
                return true;

            case R.id.item2:
                fAuth.signOut();
                delete_player_id(dataHolder.getName());
                Intent intent_2 = new Intent(fragments.this, MainActivity.class);
                finish();
                startActivity(intent_2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void delete_player_id(String own_username){
        own_username += "@pizza.com";

        final DocumentReference reference_val = db.collection("user_val").document(own_username);
        final DocumentReference reference_nicks = db.collection("user_nicks").document(own_username);

        update_field_notification_id(reference_val, "notification_id", "none");
        update_field_notification_id(reference_nicks, "notification", "none");

    }

    private void update_field_notification_id(DocumentReference reference, String field_name, String value){

        reference
                .update(field_name, value)
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

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}