package com.art2cat.dev.moonlightnote.Controller.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.InputDialogFragment;
import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.CustomView.BaseFragment;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.AESUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.AuthUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginFragment";
    protected View mView;
    private AppCompatEditText mEmailView;
    private AppCompatEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private int flag = 0;
    private boolean isNewUser = false;
    private String mEncryptKey;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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
        AppCompatButton mRegister = (AppCompatButton) mView.findViewById(R.id.email_sign_in_button);
        AppCompatButton mLogin = (AppCompatButton) mView.findViewById(R.id.email_sign_up_button);
        AppCompatButton mLogin_Google = (AppCompatButton) mView.findViewById(R.id.login_google_btn);
        AppCompatButton test = (AppCompatButton) mView.findViewById(R.id.test_btn);

        reset.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mLogin_Google.setOnClickListener(this);
        test.setOnClickListener(this);

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
    }

    @Override
    public void onStop() {
        super.onStop();
        removeListener();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
            mGoogleApiClient.disconnect();
        }
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
        if (TextUtils.isEmpty(password)) {
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
                isNewUser = true;
            } else {
                showProgress(false);
                showShortSnackBar(mView, "Google Sign In failed", SnackBarUtils.TYPE_INFO);
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
                    InputDialogFragment inputDialogFragment = InputDialogFragment
                            .newInstance(getString(R.string.dialog_reset_password), 0);
                    inputDialogFragment.show(getFragmentManager(), "resetPassword");
                } else {
                    AuthUtils.sendRPEmail(MoonlightApplication.getContext(), mView, email);
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
                GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getActivity().getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleApiClient = null;
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .enableAutoManage((FragmentActivity) getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                        .build();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.test_btn:
//                Utils.openMailClient(getActivity());
                signInAnonymously();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showShortSnackBar(mView, connectionResult.getErrorMessage(), SnackBarUtils.TYPE_ALERT);
    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SPUtils.putString(MoonlightApplication.getContext(), "User", "Id", user.getUid());
                    Uri photoUrl = user.getPhotoUrl();
                    String nickname = user.getDisplayName();
                    String email = user.getEmail();
                    String token = FirebaseInstanceId.getInstance().getToken();
                    User user1 = new User();
                    user1.setUid(user.getUid());
                    if (nickname != null) {
                        user1.setNickname(nickname);
                    }
                    if (email != null) {
                        user1.setEmail(email);
                    }
                    if (photoUrl != null) {
                        user1.setPhotoUrl(photoUrl.toString());
                    }

                    if (token != null) {
                        user1.setToken(token);
                    }

                    if (mEncryptKey != null) {
                        user1.setEncryptKey(mEncryptKey);
                    } else {
                        user1.setEncryptKey(user.getUid());
                    }

                    if (isNewUser) {
                        commitMoonlight(user.getUid());
                    }

                    UserUtils.updateUser(user.getUid(), user1);
                    UserUtils.saveUserToCache(MoonlightApplication.getContext(), user1);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    @SuppressLint("LogConditional")
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LogConditional")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            showProgress(false);
                            Log.w(TAG, "signInWithCredential", task.getException());
                            //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
                            String exception = task.getException().getMessage();
                            showShortSnackBar(mView, "Authentication failed: " + exception,
                                    SnackBarUtils.TYPE_WARNING);
                        } else {
                            showProgress(false);
                            Intent intent = new Intent(getActivity(), MoonlightActivity.class);
                            Bundle bundle = ActivityOptions
                                    .makeSceneTransitionAnimation(getActivity()).toBundle();
                            getActivity().startActivity(intent, bundle);
                            showShortSnackBar(mView, "Google Sign In succeed", SnackBarUtils.TYPE_INFO);
                            isNewUser = true;
                            SPUtils.putBoolean(MoonlightApplication.getContext(), "User", "google", true);
                            getActivity().finishAfterTransition();
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
                            Bundle bundle = ActivityOptions
                                    .makeSceneTransitionAnimation(getActivity()).toBundle();
                            getActivity().startActivity(intent, bundle);

                            //销毁当前Activity
                            getActivity().finishAfterTransition();
                            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showProgress(false);
                showShortSnackBar(mView, "Sign In Failed: " + e.toString(),
                        SnackBarUtils.TYPE_WARNING);
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
                            showLongSnackBar(mView, "Sign Up succeed!",
                                    SnackBarUtils.TYPE_WARNING);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                            }
                                        }
                                    });

                            mEncryptKey = AESUtils.generateKey();
                            signInWithEmail(email, password);
                            isNewUser = true;
                        } else {
                            showProgress(false);
                            isNewUser = false;
                            //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
                            String exception = task.getException().getMessage();
                            showLongSnackBar(mView, "Sign Up Failed: " + exception,
                                    SnackBarUtils.TYPE_WARNING);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (BuildConfig.DEBUG) Log.d(TAG, e.toString());
            }
        });
    }

    private void signInAnonymously() {
        showProgress(true);
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("LogConditional")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getActivity(), MoonlightActivity.class);
                            getActivity().startActivity(intent);
                        }

                        showProgress(false);
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        if (busEvent.getMessage().contains("@")) {
            AuthUtils.sendRPEmail(MoonlightApplication.getContext(), mView, busEvent.getMessage());
        } else {
            showShortSnackBar(mView, "Invalid email address",
                    SnackBarUtils.TYPE_WARNING);
        }
    }

    private void commitMoonlight(String userId) {
        Moonlight moonlight = new Moonlight();
        moonlight.setTitle(getString(R.string.init_moonlight_title));
        moonlight.setContent(getString(R.string.init_moonlight_content));
        FDatabaseUtils.addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
    }
}
