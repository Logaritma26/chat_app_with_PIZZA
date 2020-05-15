package com.example.chat_app.fragments;

import android.os.Bundle;

import com.example.chat_app.R;
import com.example.chat_app.fragments.ui.fragment_add_contact;
import com.example.chat_app.fragments.ui.fragment_main;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class fragments extends AppCompatActivity {

    private SectionsPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        TabLayout tabs = findViewById(R.id.tabs);
        FloatingActionButton fab = findViewById(R.id.fab);
        SearchView searchView = findViewById(R.id.searchView);
        Toolbar toolbar = findViewById(R.id.toolbar);
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
                //
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}