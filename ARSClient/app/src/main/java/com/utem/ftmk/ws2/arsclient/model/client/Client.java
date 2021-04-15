package com.utem.ftmk.ws2.arsclient.model.client;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.utem.ftmk.ws2.arsclient.model.payment.Payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Client implements Serializable {

    private String email;
    private String storeName;
    private String description;
    private String logo;
    private List<String> images;
    private Location location;
    private Subscription subscription;

    // Excluded field from firebase database
    private String password;

    public Client() {

    }

    public Client(String email, String password, String storeName, Location location) {
        this.email = email;
        this.password = password;
        this.storeName = storeName;
        this.location = location;
    }

    public Client(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    @Exclude
    public void setPassword(String password) {
        this.password = password;
    }


    public static class Location implements Serializable {

        private String id;
        private String name;
        private String address;
        private double latitude;
        private double longitude;

        public Location() {

        }

        public Location(String id, String name, String address, double latitude, double longitude) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Location(String id, String name, String address, LatLng latLng) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        @Exclude
        public LatLng getLatLng() {
            return new LatLng(latitude, longitude);
        }
    }

    public static class Subscription implements Serializable {

        private boolean status;
        private long expiredDate;
        private List<Payment> histories;

        public Subscription() {
        }

        public Subscription(boolean status, long expiredDate, List<Payment> histories) {
            this.status = status;
            this.expiredDate = expiredDate;
            this.histories = histories;
        }

        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public long getExpiredDate() {
            return expiredDate;
        }

        public void setExpiredDate(long expiredDate) {
            this.expiredDate = expiredDate;
        }

        public List<Payment> getHistories() {
            if (histories == null) {
                histories = new ArrayList<>();
            }
            return histories;
        }

        public void setHistories(List<Payment> histories) {
            this.histories = histories;
        }

    }

}
