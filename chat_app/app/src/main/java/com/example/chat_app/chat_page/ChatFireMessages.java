package com.example.chat_app.chat_page;

import androidx.annotation.Keep;

@Keep
public class ChatFireMessages {


    private String message;
    private String username;
    private Boolean seen;

    public ChatFireMessages() {
    }

    public ChatFireMessages(String message, String username, Boolean seen) {
        this.message = message;
        this.username = username;
        this.seen = seen;
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

}
