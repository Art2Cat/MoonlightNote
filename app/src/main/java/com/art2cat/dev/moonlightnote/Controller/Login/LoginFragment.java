package com.art2cat.dev.moonlightnote.Controller.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.Model.UserConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.AuthUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.UserConfigUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    protected View mView;
    private AppCompatEditText mEmailView;
    private AppCompatEditText mPasswordView;
    private AppCompatButton mRegister;
    private AppCompatButton mLogin;
    private AppCompatImageButton mLogin_Google;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myReference;
    private GoogleApiClient mGoogleApiClient;
    private int flag = 0;
    private boolean signUp_state;
    private static final int RC_SIGN_IN = 9001;
    private static final String USER_G = "google";

    private static final String TAG = "LoginFragment";


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        myReference = FirebaseDatabase.getInstance().getReference();
        EventBus.getDefault().register(this);
        signIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailView = (AppCompatEditText) mView.findViewById(R.id.email);

        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        ((LoginActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.fragment_login);

        mPasswordView = (AppCompatEditText) mView.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        AppCompatButton reset = (AppCompatButton) mView.findViewById(R.id.reset_password);
        mRegister = (AppCompatButton) mView.findViewById(R.id.email_sign_in_button);
        mLogin = (AppCompatButton) mView.findViewById(R.id.email_sign_up_button);
        mLogin_Google = (AppCompatImageButton) mView.findViewById(R.id.login_google_btn);

        reset.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mLogin_Google.setOnClickListener(this);

        mLoginFormView = mView.findViewById(R.id.login_form);
        mProgressView = mView.findViewById(R.id.login_progress);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        addListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getActivity().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();

        Log.d(TAG, "233: " + mGoogleApiClient.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
        removeListener();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the date of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) ) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (flag == 1) {
                signUp(email, password);

                flag = 2;
            } else if (flag == 2) {
                signInWithEmail(email, password);
                //Intent intent = new Intent(getActivity(), MoonlightActivity.class);
                //startActivity(intent);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult: " + result.getStatus());
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showProgress(false);
                SnackBarUtils.shortSnackBar(mView, "Google Sign In failed", SnackBarUtils.TYPE_INFO).show();
                Log.d(TAG, "Google Sign In failed");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_password:
                mEmailView.setError(null);
                String email = mEmailView.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    RPDialogFragment rpDialogFragment = new RPDialogFragment();
                    rpDialogFragment.show(getFragmentManager(), "resetPassword");
                } else {
                    AuthUtils.sendRPEmail(getActivity(), mView, email);
                }
                break;
            case R.id.email_sign_in_button:
                flag = 2;
                attemptLogin();
                break;
            case R.id.email_sign_up_button:
                flag = 1;
                attemptLogin();
                break;
            case R.id.login_google_btn:
                showProgress(true);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        SnackBarUtils.shortSnackBar(mView, connectionResult.getErrorMessage(),
                SnackBarUtils.TYPE_ALERT).show();
    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SPUtils.putString(getActivity(), "User", "Id", user.getUid());
                    Uri photoUrl = user.getPhotoUrl();
                    User user1 = new User(user.getDisplayName(), user.getEmail(), user.getUid());
                    if (photoUrl != null) {
                        user1.avatarUrl = photoUrl.toString();
                    }
                    createUserConfig(user.getUid());
                    createUser(user.getUid(), user1);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            showProgress(false);
                            Log.w(TAG, "signInWithCredential", task.getException());
                            SnackBarUtils.shortSnackBar(mView, "Authentication failed.",
                                    SnackBarUtils.TYPE_WARNING).show();
                        } else {
                            showProgress(false);
                            Intent intent = new Intent(getActivity(), MoonlightActivity.class);
                            getActivity().startActivity(intent);
                            SnackBarUtils.shortSnackBar(mView, "Google Sign In successed", SnackBarUtils.TYPE_INFO).show();
                            SPUtils.putBoolean(getActivity(), "User", "google", true);
                            getActivity().finish();
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
                            showProgress(false);
                            Intent intent = new Intent(getActivity(), MoonlightActivity.class);
                            getActivity().startActivity(intent);
                            //销毁当前Activity
                            getActivity().finish();
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        } else {
                            showProgress(false);
                            SnackBarUtils.shortSnackBar(mView, "Sign In Failed",
                                    SnackBarUtils.TYPE_WARNING).show();
                        }
                    }
                });
    }

    public void signUp(final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showProgress(false);
                            SnackBarUtils.longSnackBar(mView, "Sign Up succeed!",
                                    SnackBarUtils.TYPE_WARNING).show();
                            signInWithEmail(email, password);
                            signUp_state = true;
                        } else {
                            showProgress(false);
                            signUp_state = false;
                            SnackBarUtils.longSnackBar(mView, "Sign Up Failed: " + task.getException().toString(),
                                    SnackBarUtils.TYPE_WARNING).show();
                        }
                    }
                });
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

    private void createUserConfig(String userId) {
        UserConfig userConfig = new UserConfig();
        List<String> labels = new ArrayList<String>();
        labels.add("New Label");
        labels.add("Default");
        Map<String, Integer> colors = new HashMap<String, Integer>();
        colors.put("red", 0xfff44336);
        colors.put("green", 0xff4caf50);
        colors.put("blue", 0xff2195f3);
        userConfig.setUserId(userId);
        userConfig.setLabels(labels);
        userConfig.setColors(colors);
        UserConfigUtils.writeUserConfig(getActivity(), userConfig);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        if (busEvent.getMessage().contains("@")) {
            AuthUtils.sendRPEmail(getActivity(), mView, busEvent.getMessage());
        } else {
            SnackBarUtils.shortSnackBar(mView, "Invalid email address",
                    SnackBarUtils.TYPE_WARNING).show();
        }
    }
}
