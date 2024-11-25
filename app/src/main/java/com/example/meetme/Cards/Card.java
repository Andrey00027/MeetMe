package com.example.meetme.Cards;

public class Card
{

    private String userId;
    private String profileUrl;
    private String name;
    private String age;
    private String phone;
    private String hobbies;
    private String ocupation;
    private String socialStatus;

    public Card(String userId, String name, String profileUrl, String age, String phone, String hobbies, String ocupation, String socialStatus) {
        this.userId = userId;
        this.name = name;
        this.profileUrl = profileUrl;
        this.age = age;
        this.phone = phone;
        this.hobbies = hobbies;
        this.ocupation = ocupation;
        this.socialStatus = socialStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
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

    public String getOcupation() {
        return ocupation;
    }

    public void setOcupation(String ocupation) {
        this.ocupation = ocupation;
    }

    public String getSocialStatus() {
        return socialStatus;
    }

    public void setSocialStatus(String socialStatus) {
        this.socialStatus = socialStatus;
    }

}
