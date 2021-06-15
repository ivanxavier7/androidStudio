package com.ptda.imiser.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.helper.Base64Custom;
import com.ptda.imiser.helper.DateUtilCustom;

public class Transaction {
    private String date;
    private String category;
    private String description;
    private String type;
    private String Key;
    private double value;
    private double latitude;
    private double longitude;

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

    public Transaction() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void save(String chosenDate) {
        FirebaseAuth auth = FireBaseConfig.getFireBaseAuth();
        String idUser = Base64Custom.encodeBase64Custom(auth.getCurrentUser().getEmail());
        DatabaseReference firebase = FireBaseConfig.getFirebaseDatabase();
        String monthYear = DateUtilCustom.getDate(date);
        firebase.child("movimento")
                .child(idUser)
                .child(monthYear)
                .push()
                .setValue(this);
    }
}
