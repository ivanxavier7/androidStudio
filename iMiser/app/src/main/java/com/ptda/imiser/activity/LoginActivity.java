package com.ptda.imiser.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.ptda.imiser.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void btnLogin(View view) {
        startActivity(new Intent(this, LoginFormActivity.class));
    }

    public void btnRegister(View view) {
        startActivity(new Intent(this, RegisterFormActivity.class));
    }
}