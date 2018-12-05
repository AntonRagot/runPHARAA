package ch.epfl.sweng.runpharaa.login.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationReal extends FirebaseAuthentication {
    @Override
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public Task<AuthResult> signInWithCredential(AuthCredential c) {
        return FirebaseAuth.getInstance().signInWithCredential(c);
    }
}
