package com.ptda.imiser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.ptda.imiser.R;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.model.UserModel;

public class LoginFormActivity extends AppCompatActivity {
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button loginBtn;
    private ConstraintLayout loginLayout;
    private FirebaseAuth auth;
    private UserModel user;
    private View displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        // Login View
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginLayout = findViewById(R.id.loginLayout);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                displayView = view;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginLayout.getWindowToken(), 0);
                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();

                if (!email.isEmpty()) {
                    emailLogin.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                    if (!password.isEmpty()) {
                        passwordLogin.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                        user = new UserModel();
                        user.setEmail(email);
                        user.setPassword(password);

                        loginUser();
                    } else {
                        passwordLogin.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                        Snackbar.make(
                                view,
                                "Por favor, introduza a sua password!",
                                Snackbar.LENGTH_SHORT)
                                .setTextColor(getResources().getColor(R.color.colorError))
                                .show();
                    }
                } else {
                    emailLogin.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                    Snackbar.make(
                            view,
                            "Por favor, introduza o seu email!",
                            Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.colorError))
                            .show();
                }
            }
        });
    }

    public void loginUser() {
        auth = FireBaseConfig.getFireBaseAuth();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ) {
                    if(auth.getCurrentUser().isEmailVerified()) {
                        startApp();
                    } else {
                        Snackbar.make(
                                displayView,
                                "Por favor verifique o seu Email!",
                                Snackbar.LENGTH_SHORT)
                                .setTextColor(getResources().getColor(R.color.colorAccent))
                                .show();
                    }
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailLogin.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                        exception = "Utilizador não existe!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailLogin.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                        passwordLogin.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                        exception = "O e-mail e a password não correspondem!";
                    } catch (Exception e) {
                        exception = "Erro a entrar na conta: " + e.getMessage();
                        e.printStackTrace();    //Print da excessao no log
                    }

                    Snackbar.make(
                            displayView,
                            exception,
                            Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.colorError))
                            .show();
                }
            }
        });
    }

    public void startApp() {
        startActivity(new Intent(this, MainAppActivity.class));
        finish();   // Close login activity
    }
}