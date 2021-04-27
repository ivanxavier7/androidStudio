package com.ptda.imiser.model;

import android.database.DatabaseUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.helper.Base64Custom;

import java.util.Base64;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String password;
    private Double depositoTotal = 0.00;
    private Double levantamentoTotal = 0.00;

    public UserModel() {
    }

    @Exclude
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getDepositoTotal() {
        return depositoTotal;
    }

    public void setDepositoTotal(Double depositoTotal) {
        this.depositoTotal = depositoTotal;
    }

    public Double getLevantamentoTotal() {
        return levantamentoTotal;
    }

    public void setLevantamentoTotal(Double levantamentoTotal) {
        this.levantamentoTotal = levantamentoTotal;
    }


    public void save() {
        DatabaseReference firebase = FireBaseConfig.getFirebaseDatabase();
        firebase.child("usuarioB64")
                .child(this.id)
                .setValue(this);
    }
}
