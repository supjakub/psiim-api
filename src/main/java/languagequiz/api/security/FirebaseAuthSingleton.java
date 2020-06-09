package languagequiz.api.security;

import com.google.firebase.auth.FirebaseAuth;

public final class FirebaseAuthSingleton {
    private static FirebaseAuthSingleton INSTANCE;
    private FirebaseAuth firebaseAuth;

    private FirebaseAuthSingleton() {
        firebaseAuth = FirebaseAuth.getInstance(FirebaseAppSingleton.getInstance().getFirebaseApp());
    }

    public static FirebaseAuthSingleton getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FirebaseAuthSingleton();
        }
        return INSTANCE;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
