package com.example.meetme.Matched;

import java.util.ArrayList;

public class Matched
{

    private String userId, name, profileImageUrl, occupation, lastMessage, lastTimeStamp, lastSeen, childId;
    private ArrayList<Matched> userObjectArrayList = new ArrayList<>();

    public Matched(String userId, String name, String profileImageUrl, String occupation, String lastMessage, String lastTimeStamp, String lastSeen, String childId)
    {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.occupation = occupation;
        this.lastMessage = lastMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.lastSeen = lastSeen;
        this.childId = childId;
    }

    public ArrayList<Matched> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    public void addUserToArrayList(Matched user) {
        userObjectArrayList.add(user);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

}
