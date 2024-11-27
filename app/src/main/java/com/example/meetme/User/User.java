package com.example.meetme.User;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Recomendación para Serializable

    private String userId;
    private String name;
    private String age;
    private String sex;
    private String phone;
    private String hobbies;
    private String occupation;
    private String socialStatus;
    private String notificationKey;
    private boolean selected = false;

    // Constructor con userId
    public User(String userId) {
        this.userId = userId;
    }

    // Constructor completo
    public User(String age, String name, String sex, String phone, String hobbies, String occupation, String socialStatus, String notificationKey) {
        this.age = age;
        this.name = name;
        this.sex = sex;
        this.phone = phone;
        this.hobbies = hobbies;
        this.occupation = occupation;
        this.socialStatus = socialStatus;
        this.notificationKey = notificationKey;
    }

    // Getters y Setters
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSocialStatus() {
        return socialStatus;
    }

    public void setSocialStatus(String socialStatus) {
        this.socialStatus = socialStatus;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
