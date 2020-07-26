package com.example.chat_app.chat_page;

import androidx.annotation.Keep;

import com.google.firebase.Timestamp;

@Keep
public class ChatFireMessages {


    private String message;
    private String username;
    private Boolean seen;
    private Timestamp time;

    public ChatFireMessages() {
    }

    public ChatFireMessages(String message, String username, Boolean seen, Timestamp time) {
        this.message = message;
        this.username = username;
        this.seen = seen;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public Timestamp getTime() {
        return time;
    }
}
