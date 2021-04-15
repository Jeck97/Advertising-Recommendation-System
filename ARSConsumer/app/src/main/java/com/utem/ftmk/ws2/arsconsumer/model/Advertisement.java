package com.utem.ftmk.ws2.arsconsumer.model;


import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Advertisement implements Serializable {

    public static final String FIREBASE_IDENTIFIER = "advertisement";

    public static final String STATUS_ACTIVATED = "advertisement_status_activated";
    public static final String STATUS_REJECTED = "advertisement_status_rejected";
    public static final String STATUS_PENDING = "advertisement_status_pending";
    public static final String STATUS_EXPIRED = "advertisement_status_expired";

    private String id;
    private String status;
    private String title;
    private String description;
    private List<Integer> categories;
    private List<Like> likes;
    private long uploadedDate;
    private long postedDate;
    private String coverImage;
    private List<String> images;

    private String ownerId;
    private String merchantName;
    private Client.Location merchantLocation;
    private double distance;

    public Advertisement() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public List<Like> getLikes() {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public long getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(long uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public long getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(long postedDate) {
        this.postedDate = postedDate;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Exclude
    public String getMerchantName() {
        return merchantName;
    }

    @Exclude
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    @Exclude
    public Client.Location getMerchantLocation() {
        return merchantLocation;
    }

    @Exclude
    public void setMerchantLocation(Client.Location merchantLocation) {
        this.merchantLocation = merchantLocation;
    }

    @Exclude
    public double getDistance() {
        return distance;
    }

    @Exclude
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static class Like implements Serializable {

        private long likedDate;
        private String gender;
        private long dob;
        private String likerId;

        public Like() {

        }

        public Like(String gender, long dob, String likerId) {
            this.likedDate = System.currentTimeMillis();
            this.gender = gender;
            this.dob = dob;
            this.likerId = likerId;
        }

        public long getLikedDate() {
            return likedDate;
        }

        public void setLikedDate(long likedDate) {
            this.likedDate = likedDate;
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

        public String getLikerId() {
            return likerId;
        }

        public void setLikerId(String likerId) {
            this.likerId = likerId;
        }
    }

}
