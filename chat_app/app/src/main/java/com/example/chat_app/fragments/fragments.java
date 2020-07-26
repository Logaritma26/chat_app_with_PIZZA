package com.example.chat_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.chat_app.ContainerMethods;
import com.example.chat_app.click_manager.DeleteButtonVisibleFragments;
import com.example.chat_app.click_manager.IOBackPressed;
import com.example.chat_app.OwnData;
import com.example.chat_app.fragments.ui.contact_list.FireContactList;
import com.example.chat_app.fragments.ui.pending.fragment_accept;
import com.example.chat_app.R;
import com.example.chat_app.fragments.ui.search.SearchDataHolder;
import com.example.chat_app.fragments.ui.search.fragment_add_contact;
import com.example.chat_app.fragments.ui.home_page.fragment_main;
import com.example.chat_app.menu.profile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class fragments extends AppCompatActivity implements DeleteButtonVisibleFragments {

    public static ViewPager mViewPager;
    SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    SearchDataHolder searchDataHolder;
    OwnData ownData;

    ConstraintLayout layout_toolbar;
    SearchView searchView;
    TabLayout tabs;
    Toolbar toolbar;
    Button delete_button;
    ImageView pp_home;
    ImageView icon_long;
    CardView icon_main;


    int last_key_lenght = 0;
    public static boolean still_running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        searchDataHolder = SearchDataHolder.getInstance();
        ownData = OwnData.getInstance();

        get_shared();
        init_oneSignal();

        set_up_elements_ui();

        search_methods(searchView);

        ContainerMethods.set_last_seen_if(db, 0);
    }

    private void get_shared() {
        SharedPreferences sharedPref = getSharedPreferences("Username", Context.MODE_PRIVATE);
        String username_shared = sharedPref.getString("username", "none");
        String url = sharedPref.getString("pp_url", "none");
        ownData.setName(username_shared);
        ownData.setPic_url(url);
    }

    private void init_oneSignal() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        if (ownData.getNotification_ch().equals("") || ownData.getNotification_ch().equals("none")){
            final DocumentReference reference_val = db.collection("user_val").document(ownData.getName() + "@pizza.com");
            final DocumentReference reference_nicks = db.collection("user_nicks").document(ownData.getName() + "@pizza.com");

            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    ContainerMethods.update_field(reference_val, "notification_id", userId);
                    ContainerMethods.update_field(reference_nicks, "notification", userId);
                    ownData.setNotification_ch(userId);
                }
            });
        }

    }

    private void request_power_ignore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent myIntent = new Intent();
                myIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(myIntent);
            }
        }
    }

    private void set_up_elements_ui() {
        pp_home = findViewById(R.id.image_pp_home);
        if (!ownData.getPic_url().equals("none") && !ownData.getPic_url().equals("")) {
            Glide
                    .with(fragments.this)
                    .load(ownData.getPic_url())
                    .centerCrop()
                    .into(pp_home);
        }
        pp_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                still_running = true;
                Intent intent_1 = new Intent(fragments.this, profile.class);
                startActivity(intent_1);
            }
        });

        icon_main = findViewById(R.id.icon_main);
        icon_long = findViewById(R.id.icon_long);
        layout_toolbar = findViewById(R.id.layout_toolbar);
        tabs = findViewById(R.id.tabs);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        delete_button();

        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);


    }

    private void delete_button() {
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
                } else if (FireContactList.is_long_clicked) {
                    for (int i = 0; i < FireContactList.selected_adapter_position.size(); i++) {
                        try {
                            FireContactList.recyclerView.findViewHolderForAdapterPosition(FireContactList.selected_adapter_position.get(i)).itemView.findViewById(R.id.view_cover).setVisibility(View.GONE);
                        } catch (Exception ignored) {
                        }
                    }

                    ContainerMethods.delete_friends(db, FireContactList.selected_username);
                    ContainerMethods.delete_chat(db, FireContactList.selected_username);
                    FireContactList.is_long_clicked = false;
                    delete_button.setVisibility(View.GONE);
                }
            }
        });
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
        adapter.addFragment(new FireContactList(this), "Contacts");
        adapter.addFragment(new fragment_add_contact(), "Search");
        adapter.addFragment(new fragment_accept(), "Pending");
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(mViewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 2) {
                    searchView.setVisibility(View.GONE);
                    icon_long.setVisibility(View.VISIBLE);
                    icon_main.setVisibility(View.GONE);
                    layout_toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    searchView.setVisibility(View.VISIBLE);
                    icon_long.setVisibility(View.GONE);
                    icon_main.setVisibility(View.VISIBLE);
                    layout_toolbar.setBackgroundResource(R.drawable.curve_search);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        still_running = false;
        ContainerMethods.set_last_seen_if(db, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ownData.getPic_url().equals("none") && !ownData.getPic_url().equals("")) {
            Glide
                    .with(fragments.this)
                    .load(ownData.getPic_url())
                    .centerCrop()
                    .into(pp_home);
        }

        still_running = false;
        ContainerMethods.set_last_seen_if(db, 0);
    }

    @Override
    public void DeleteVisible() {
        delete_button.setVisibility(View.VISIBLE);
    }

    @Override
    public void DeleteGone() {
        delete_button.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!still_running) {
            ContainerMethods.set_last_seen_if(db, 1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!still_running) {
            ContainerMethods.set_last_seen_if(db, 1);
        }
    }

}