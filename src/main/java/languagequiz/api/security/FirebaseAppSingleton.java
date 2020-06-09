package languagequiz.api.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class FirebaseAppSingleton {
    private static FirebaseAppSingleton INSTANCE;
    private FirebaseApp firebaseApp;

    private FirebaseAppSingleton() {
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(
                    "src/main/resources/language-quiz-69d89-firebase-adminsdk-1ol4w-3f9cbfd0d1.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://language-quiz-69d89.firebaseio.com")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        firebaseApp = FirebaseApp.initializeApp(options);
    }

    public static FirebaseAppSingleton getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new FirebaseAppSingleton();
        }
        return INSTANCE;
    }

    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }
}
