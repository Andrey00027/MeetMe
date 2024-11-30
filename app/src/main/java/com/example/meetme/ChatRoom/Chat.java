package com.example.meetme.ChatRoom;

public class Chat
{

    private String message;
    private Boolean currentUser;
    private Boolean isSeen;

    public Chat(String message, Boolean currentUser, Boolean isSeen) {
        this.message = message;
        this.currentUser = currentUser;
        this.isSeen = isSeen;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
