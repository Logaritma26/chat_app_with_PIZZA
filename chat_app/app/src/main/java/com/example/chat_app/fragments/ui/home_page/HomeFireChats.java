package com.example.chat_app.fragments.ui.home_page;

import com.google.firebase.Timestamp;

public class HomeFireChats {

    private String username;
    private String message;
    private String pp_url;
    private Timestamp time;

    public HomeFireChats() {
    }

    public HomeFireChats(String username, String message, String pp_url, Timestamp time) {
        this.username = username;
        this.message = message;
        this.pp_url = pp_url;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getPp_url() {
        return pp_url;
    }

    public Timestamp getTime() {
        return time;
    }
}
