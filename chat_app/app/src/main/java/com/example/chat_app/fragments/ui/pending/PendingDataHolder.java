package com.example.chat_app.fragments.ui.pending;

import java.util.ArrayList;
import java.util.List;

public class PendingDataHolder {

    private List<String> username = new ArrayList<>();
    private List<String> status = new ArrayList<>();
    private List<String> pic_url = new ArrayList<>();
    private List<String> notification = new ArrayList<>();
    private List<Boolean> status_permissions = new ArrayList<>();
    private List<Boolean> pic_permissions = new ArrayList<>();


    private static PendingDataHolder pendingDataHolder;

    private PendingDataHolder() {
    }

    public List<String> getNotification() {
        return notification;
    }

    public void addNotification(String notification) {
        this.notification.add(notification);
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

    public void addPic_permissions(boolean permission){
        this.pic_permissions.add(permission);
    }


    public void clean_at(int position){
        pendingDataHolder.getStatus().remove(position);
        pendingDataHolder.getPic_url().remove(position);
        pendingDataHolder.getUsername().remove(position);
        pendingDataHolder.getNotification().remove(position);
        pendingDataHolder.getStatus_permissions().remove(position);
        pendingDataHolder.getPic_permissions().remove(position);
    }


    public void clean(){
        pendingDataHolder.getStatus().clear();
        pendingDataHolder.getPic_url().clear();
        pendingDataHolder.getUsername().clear();
        pendingDataHolder.getNotification().clear();
        pendingDataHolder.getStatus_permissions().clear();
        pendingDataHolder.getPic_permissions().clear();
    }



    public static PendingDataHolder getInstance(){
        if (pendingDataHolder == null){
            pendingDataHolder = new PendingDataHolder();
        }
        return pendingDataHolder;
    }
}
