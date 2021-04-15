package com.utem.ftmk.ws2.arsconsumer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Consumer implements Serializable {

    public static final String FIREBASE_IDENTIFIER = "consumer";

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    private String id;
    private String email;
    private String username;
    private String gender;
    private long dob;
    private String profileImage;
    private List<String> likedAdvertisements;

    public Consumer() {

    }

    public Consumer(String id, String email, String username, String gender, long dob) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.dob = dob;
        this.profileImage = null;
        this.likedAdvertisements = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<String> getLikedAdvertisements() {
        if (likedAdvertisements == null) {
            likedAdvertisements = new ArrayList<>();
        }
        return likedAdvertisements;
    }

    public void setLikedAdvertisements(List<String> likedAdvertisements) {
        this.likedAdvertisements = likedAdvertisements;
    }
}
