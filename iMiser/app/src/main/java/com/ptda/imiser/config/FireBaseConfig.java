package com.ptda.imiser.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseConfig {
    private static FirebaseAuth auth;
    private static DatabaseReference firebase;

    // Devolve objeto de autenticacao
    public static FirebaseAuth getFireBaseAuth() {
        if ( auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    // Devolve objeto da base de dados
    public static DatabaseReference getFirebaseDatabase() {
        if (firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }
}
