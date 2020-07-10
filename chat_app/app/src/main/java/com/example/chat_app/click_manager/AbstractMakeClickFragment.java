package com.example.chat_app.click_manager;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public abstract class AbstractMakeClickFragment extends Fragment implements Recycler_Click, IOBackPressed {



    @Override
    public void OnRecyclerClickListener(View view, int position, TextView username) {

    }

    @Override
    public void OnRecyclerClickListener(int position) {

    }

    @Override
    public void OnRecyclerClickListener(String username, String status, String pp_url) {

    }

    @Override
    public void OnDeclineClickListener(int position) {

    }

    @Override
    public void OnRecyclerClickListener(String username, String pp_url) {

    }

    @Override
    public void OnRecyclerClickListener(String username, String pp_url, View view, int position) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onBackPressed(View view) {
        return false;
    }
}
