package com.example.chat_app;


import java.util.ArrayList;
import java.util.List;

public class OwnData {

    private String name = "none";
    private String pic_url = "none";
    private Boolean pic_permission = false;
    private String status = "Available";
    private Boolean status_permission = true;
    private String notification_ch = "none";
    private Integer seen = 0;
    private List<String> friendList = new ArrayList<>();
    private List<String> requests_sent_list = new ArrayList<>();

    private static OwnData ownData;

    private OwnData() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public Boolean getPic_permission() {
        return pic_permission;
    }

    public void setPic_permission(Boolean pic_permission) {
        this.pic_permission = pic_permission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getStatus_permission() {
        return status_permission;
    }

    public void setStatus_permission(Boolean status_permission) {
        this.status_permission = status_permission;
    }

    public String getNotification_ch() {
        return notification_ch;
    }

    public void setNotification_ch(String notification_ch) {
        this.notification_ch = notification_ch;
    }

    public Integer getSeen() {
        return seen;
    }

    public void setSeen(Integer seen) {
        this.seen = seen;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void addFriendList(String friendList) {
        this.friendList.add(friendList);
    }

    public List<String> getRequests_sent_list() {
        return requests_sent_list;
    }

    public void addRequests_sent_list(String requests_sent) {
        this.requests_sent_list.add(requests_sent);
    }



    public static OwnData getOwnData() {
        return ownData;
    }

    public static void setOwnData(OwnData ownData) {
        OwnData.ownData = ownData;
    }

    public static OwnData getInstance() {
        if (ownData == null){
            ownData = new OwnData();
        }
        return ownData;
    }


}
