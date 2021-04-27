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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.ptda.imiser.R;
import com.ptda.imiser.config.FireBaseConfig;
import com.ptda.imiser.helper.Base64Custom;
import com.ptda.imiser.model.UserModel;

public class RegisterFormActivity extends AppCompatActivity {
    private EditText nameRegister;
    private EditText emailRegister;
    private EditText emailLogin;
    private EditText passwordRegister;
    private EditText passwordLogin;
    private EditText repeatRegister;
    private Button registerBtn;
    private ConstraintLayout registerLayout;
    private FirebaseAuth firebaseAuth;
    private UserModel user;
    private View displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);
        // Register View
        nameRegister = findViewById(R.id.nameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        repeatRegister = findViewById(R.id.repeatRegister);
        registerBtn = findViewById(R.id.createBtn);
        registerLayout = findViewById(R.id.registerLayout);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                displayView = view;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(registerLayout.getWindowToken(), 0);
                String name = nameRegister.getText().toString();
                String email = emailRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                String passwordRepeat = repeatRegister.getText().toString();
                // Falta validar
                if (!name.isEmpty()) {
                    nameRegister.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                    if (!email.isEmpty()) {
                        emailRegister.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                        if (!password.isEmpty()) {
                            passwordRegister.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                            if (!passwordRepeat.isEmpty()) {
                                repeatRegister.setHintTextColor(getResources().getColor(R.color.colorTransparentWhite));
                                // Success zone
                                if (password.equals(passwordRepeat)) {
                                    repeatRegister.setTextColor(getResources().getColor(R.color.colorTransparentWhite));
                                    passwordRegister.setTextColor(getResources().getColor(R.color.colorTransparentWhite));
                                    user = new UserModel();
                                    user.setName(name);
                                    user.setEmail(email);
                                    user.setPassword(password);
                                    registerUser();
                                } else {
                                    repeatRegister.setTextColor(getResources().getColor(R.color.colorError));
                                    passwordRegister.setTextColor(getResources().getColor(R.color.colorError));
                                    Snackbar.make(
                                            view,
                                            "As passwords inseridas não coincidem!",
                                            Snackbar.LENGTH_SHORT)
                                            .setTextColor(getResources().getColor(R.color.colorError))
                                            .show();
                                }
                            } else {
                                repeatRegister.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                                Snackbar.make(
                                        view,
                                        "Por favor, volte a introduzir a password!",
                                        Snackbar.LENGTH_SHORT)
                                        .setTextColor(getResources().getColor(R.color.colorError))
                                        .show();
                            }
                        } else {
                            passwordRegister.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                            Snackbar.make(
                                    view,
                                    "Por favor, introduza a sua password!",
                                    Snackbar.LENGTH_SHORT)
                                    .setTextColor(getResources().getColor(R.color.colorError))
                                    .show();
                        }
                    } else {
                        emailRegister.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                        Snackbar.make(
                                view,
                                "Por favor, introduza o seu email!",
                                Snackbar.LENGTH_SHORT)
                                .setTextColor(getResources().getColor(R.color.colorError))
                                .show();
                    }
                } else {
                    nameRegister.setHintTextColor(getResources().getColor(R.color.colorSnackBar));
                    Snackbar.make(
                            view,
                            "Por favor introduza o seu nome!",
                            Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.colorError)
                            ).show();
                }
            }
        });
    }

    public void startLogin() {
        startActivity(new Intent(this, LoginFormActivity.class));
    }

    public void registerUser() {
        firebaseAuth = FireBaseConfig.getFireBaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ) {
                    String idUser = Base64Custom.encodeBase64Custom(user.getEmail());
                    user.setId(idUser);
                    user.save();
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                startLogin();
                            } else {
                                Snackbar.make(
                                        displayView,
                                        task.getException().getMessage(),
                                        Snackbar.LENGTH_SHORT)
                                        .setTextColor(getResources().getColor(R.color.colorError))
                                        .show();
                            }
                        }
                    });
                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exception = "Introduza uma password melhor!";
                    } catch (FirebaseAuthEmailException e) {
                        exception = "Introduza uma email válido!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exception = "Este email já está a ser utilizado!";
                    } catch (Exception e) {
                        exception = "Erro a registar o utilizador: " + e.getMessage();
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
}