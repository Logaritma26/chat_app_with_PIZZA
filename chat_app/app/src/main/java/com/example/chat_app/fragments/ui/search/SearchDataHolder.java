package com.example.chat_app.fragments.ui.search;

import com.example.chat_app.ContainerMethods;

import java.util.ArrayList;
import java.util.List;

public class SearchDataHolder {

    private String name = "";
    private List<String> username = new ArrayList<>();
    private List<String> status = new ArrayList<>();
    private List<String> pic_url = new ArrayList<>();
    private List<String> notification_url = new ArrayList<>();
    private List<Boolean> status_permissions = new ArrayList<>();
    private List<Boolean> pic_permissions = new ArrayList<>();


    private static SearchDataHolder dataHolder;

    private SearchDataHolder() {
    }


    public List<String> getNotification_url() {
        return notification_url;
    }

    public void addNotification_url(String notification_url) {
        this.notification_url.add(notification_url);
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

    public void addStatus(String status) {
        this.status.add(status);
    }

    public List<String> getPic_url() {
        return pic_url;
    }

    public void addPic_url(String pic_url) {
        this.pic_url.add(pic_url);
    }

    public List<Boolean> getStatus_permissions() {
        return status_permissions;
    }

    public  void addStatus_permissions(boolean permission){
        this.status_permissions.add(permission);
    }

    public List<Boolean> getPic_permissions() {
        return pic_permissions;
    }

    public  void addPic_permissions(boolean permission){
        this.pic_permissions.add(permission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void clean(){
        dataHolder.getStatus().clear();
        dataHolder.getPic_url().clear();
        dataHolder.getUsername().clear();
        dataHolder.getStatus_permissions().clear();
        dataHolder.getPic_permissions().clear();
        dataHolder.getNotification_url().clear();
    }



    public static SearchDataHolder getInstance(){
        if (dataHolder == null){
            dataHolder = new SearchDataHolder();
        }
        return dataHolder;
    }

}
