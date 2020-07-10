package com.example.chat_app.click_manager;

import android.view.View;
import android.widget.TextView;

public interface Recycler_Click {

    void OnRecyclerClickListener(int position);

    void OnRecyclerClickListener(String username, String status, String pp_url);

    void OnRecyclerClickListener(String username, String pp_url);

    void OnRecyclerClickListener(String username, String pp_url, View view, int position);

    void OnRecyclerClickListener(View view, int position, TextView username);

    void OnDeclineClickListener(int position);

}
