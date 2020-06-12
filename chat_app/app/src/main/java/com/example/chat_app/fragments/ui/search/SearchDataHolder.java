package com.example.chat_app.fragments.ui.search;

import java.util.ArrayList;
import java.util.List;

public class SearchDataHolder {

    private List<String> username = new ArrayList<>();
    private List<String> status = new ArrayList<>();
    private List<String> pic_url = new ArrayList<>();

    private static SearchDataHolder dataHolder;

    private SearchDataHolder() {
    }

    public List<String> getUsername() {
        return username;
    }

    public void addUsername(String username) {
        this.username.add(username);
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getPic_url() {
        return pic_url;
    }

    public void setPic_url(List<String> pic_url) {
        this.pic_url = pic_url;
    }

    public void clean(){
        dataHolder.getStatus().clear();
        dataHolder.getPic_url().clear();
        dataHolder.getUsername().clear();

    }

    public static SearchDataHolder getInstance(){
        if (dataHolder == null){
            dataHolder = new SearchDataHolder();
        }
        return dataHolder;
    }

}
