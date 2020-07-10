package com.example.chat_app.fragments.ui.contact_list;

import androidx.annotation.Keep;

@Keep
public class ContactInformation {

    public String username;
    public String status;
    public String pp;

    public ContactInformation() {
    }

    public ContactInformation(String username, String status, String pp) {
        this.username = username;
        this.status = status;
        this.pp = pp;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public String getPp() {
        return pp;
    }


}
