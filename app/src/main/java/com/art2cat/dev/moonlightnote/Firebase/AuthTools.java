package com.art2cat.dev.moonlightnote.Firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Controller.MoonlightActivity.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by art2cat
 * on 8/12/16.
 */
public class AuthTools {
    private static final String TAG = "AuthTools";
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myReference;
    private boolean signUp_state;

    public AuthTools(Context context) {
        mAuth = FirebaseAuth.getInstance();
        myReference = FirebaseDatabase.getInstance().getReference();
        this.context = context;
    }

    public void signIn() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SPUtils.putString(context, "User", "Id", user.getUid());
                    User user1 = new User(user.getDisplayName(), user.getEmail(), user.getUid());
                    createUser(user.getUid(), user1);
                } else {
                    //flag = 1;
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signInWithEmail(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(context, MoonlightActivity.class);
                            context.startActivity(intent);
                            //销毁当前Activity
                            ((Activity)context).finish();
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        } else {
                            Toast.makeText(context, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signUp_state = true;
                        } else {
                            signUp_state = false;
                            Toast.makeText(context, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return signUp_state;
    }

    public void addListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createUser(String userId, User user) {
        Log.d(TAG, "createUser: ");
        myReference.child("user").push();

        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user/" + userId, userValues);

        myReference.updateChildren(childUpdates);
    }
}
