package com.art2cat.dev.moonlightnote.controller.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.InputDialogFragment;
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.AuthUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener {

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
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    mView = inflater.inflate(R.layout.fragment_login, container, false);

    mEmailView = mView.findViewById(R.id.email);

    Toolbar toolbar = mView.findViewById(R.id.toolbar);
    ((LoginActivity) activity).setSupportActionBar(toolbar);
    toolbar.setTitle(R.string.fragment_login);

    mPasswordView = mView.findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
      if (id == R.id.login || id == EditorInfo.IME_NULL) {
        attemptLogin();
        return true;
      }
      return false;
    });

    AppCompatButton reset = mView.findViewById(R.id.reset_password);
    AppCompatButton mRegister = mView.findViewById(R.id.email_sign_in_button);
    AppCompatButton mLogin = mView.findViewById(R.id.email_sign_up_button);
    AppCompatButton mLogin_Google = mView.findViewById(R.id.login_google_btn);
    AppCompatButton test = mView.findViewById(R.id.test_btn);

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
    if (Objects.nonNull(mGoogleApiClient)) {
      mGoogleApiClient.stopAutoManage((FragmentActivity) activity);
      mGoogleApiClient.disconnect();
    }
    super.onDestroy();
  }

  /**
   * Attempts to sign in or register the account specified by the login form. If there are form
   * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
   * attempt is made.
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
        //Intent intent = new Intent(activity, MoonlightDetailActivity.class);
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

  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {

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
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);
        isNewUser = true;
      } else {
        showProgress(false);
        showShortSnackBar(mView, "Google Sign In failed", SnackBarUtils.TYPE_INFO);
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
          inputDialogFragment.show(activity.getFragmentManager(), "resetPassword");
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
        GoogleSignInOptions mGoogleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = null;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
            .enableAutoManage((FragmentActivity) activity,
                this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
            .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        break;
      case R.id.test_btn:
        Utils.openMailClient(activity);
//                signInAnonymously();
        break;
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    showShortSnackBar(mView, connectionResult.getErrorMessage(), SnackBarUtils.TYPE_ALERT);
  }

  public void signIn() {
    mAuthListener = firebaseAuth -> {
      FirebaseUser user = firebaseAuth.getCurrentUser();
      if (Objects.nonNull(user)) {
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        SPUtils.putString(MoonlightApplication.getContext(),
            "User", "Id", user.getUid());
        Uri photoUrl = user.getPhotoUrl();
        String nickname = user.getDisplayName();
        String email = user.getEmail();
        String token = FirebaseInstanceId.getInstance().getToken();
        User user1 = new User();
        user1.setUid(user.getUid());
        if (Objects.nonNull(nickname)) {
          user1.setNickname(nickname);
        }
        if (Objects.nonNull(email)) {
          user1.setEmail(email);
        }
        if (Objects.nonNull(photoUrl)) {
          user1.setPhotoUrl(photoUrl.toString());
        }

        if (Objects.nonNull(token)) {
          user1.setToken(token);
        }

        user1.setEncryptKey(user.getUid());

        if (isNewUser) {
          commitMoonlight(user.getUid());
        }

        UserUtils.updateUser(user.getUid(), user1);
        UserUtils.saveUserToCache(MoonlightApplication.getContext(), user1);
      } else {
        Log.d(TAG, "onAuthStateChanged:signed_out:");
      }
    };
  }

  @SuppressLint("LogConditional")
  public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(task -> {
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
            Intent intent = new Intent(activity, MoonlightActivity.class);
            Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(activity).toBundle();
            activity.startActivity(intent, bundle);
            showShortSnackBar(mView, "Google Sign In succeed", SnackBarUtils.TYPE_INFO);
            isNewUser = true;
            SPUtils.putBoolean(MoonlightApplication.getContext(), "User", "google", true);
            activity.finishAfterTransition();
          }
        });
  }

  public void signInWithEmail(final String email, final String password) {
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            showProgress(false);
            Intent intent = new Intent(activity, MoonlightActivity.class);
            Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(activity).toBundle();
            activity.startActivity(intent, bundle);

            activity.finishAfterTransition();
            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
          }
        }).addOnFailureListener(e -> {
      showProgress(false);
      showShortSnackBar(mView, "Sign In Failed: " + e.toString(),
          SnackBarUtils.TYPE_WARNING);
    });
  }

  public void signUp(final String email, final String password) {

    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            showProgress(false);
            showLongSnackBar(mView, "Sign Up succeed!",
                SnackBarUtils.TYPE_WARNING);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert Objects.nonNull(user);
            user.sendEmailVerification()
                .addOnCompleteListener(task1 -> {
                  if (task1.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                  }
                });

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
        }).addOnFailureListener(e -> {
      if (BuildConfig.DEBUG) {
        Log.d(TAG, e.toString());
      }
    });
  }

  private void signInAnonymously() {
    showProgress(true);
    mAuth.signInAnonymously()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Intent intent = new Intent(activity, MoonlightActivity.class);
            activity.startActivity(intent);
          }

          showProgress(false);
        });
  }


  public void addListener() {
    mAuth.addAuthStateListener(mAuthListener);
  }

  public void removeListener() {
    if (Objects.nonNull(mAuthListener)) {
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
