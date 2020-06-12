package com.example.chat_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import com.example.chat_app.login.MainActivity;
import com.example.chat_app.R;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.example.chat_app.fragments.ui.fragment_main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    private SectionsPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    SearchDataHolder dataHolder;

    int last_key_lenght = 0;

    TabLayout tabs;
    TextView title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dataHolder = SearchDataHolder.getInstance();

        title = findViewById(R.id.title);
        tabs = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        SearchView searchView = findViewById(R.id.searchView);

        setSupportActionBar(toolbar);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);
        tabs.setupWithViewPager(mViewPager);


        search_methods(searchView);

    }




    private void search_methods(SearchView searchView) {
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabs.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
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
                        find_user(newText);
                        last_key_lenght = newText.length();
                    } else {
                        dataHolder.clean();
                        find_user(newText);
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

    private void find_user(final String item){

        db.collection("user_nicks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("get data for search", "Success");

                                String holder = null;

                                try {
                                    holder = (String) document.get("username");
                                }catch (Exception e){
                                    Log.d("data null search", e.getMessage());
                                }

                                if (holder != null){
                                    if (holder.contains(item)){
                                        dataHolder.addUsername(holder);
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

    private void  setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new fragment_main(), "CHATS");
        adapter.addFragment(new fragment_add_contact(), "SEARCH");
        adapter.addFragment(new fragment_main(), "PENDING");
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
                //
                return true;

            case R.id.item2:
                fAuth.signOut();
                Intent intent = new Intent(fragments.this, MainActivity.class);
                finish();
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }



}