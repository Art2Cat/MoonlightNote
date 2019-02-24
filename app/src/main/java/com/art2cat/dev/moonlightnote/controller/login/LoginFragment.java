package com.art2cat.dev.moonlightnote.controller.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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
  protected View view;
  private AppCompatEditText emailView;
  private AppCompatEditText passwordView;
  private View progressView;
  private View loginFormView;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;
  private GoogleApiClient googleApiClient;
  private boolean isNewUser = false;

  public LoginFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    firebaseAuth = FirebaseAuth.getInstance();
    EventBus.getDefault().register(this);
    signIn();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    view = inflater.inflate(R.layout.fragment_login, container, false);

    emailView = view.findViewById(R.id.email);

    Toolbar toolbar = view.findViewById(R.id.toolbar);
    ((LoginActivity) activity).setSupportActionBar(toolbar);
    toolbar.setTitle(R.string.fragment_login);

    passwordView = view.findViewById(R.id.password);
    passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
      if (id == R.id.login || id == EditorInfo.IME_NULL) {
        attemptLogin(false);
        return true;
      }
      return false;
    });

    AppCompatButton reset = view.findViewById(R.id.reset_password);
    AppCompatButton mRegister = view.findViewById(R.id.email_sign_in_button);
    AppCompatButton mLogin = view.findViewById(R.id.email_sign_up_button);
    AppCompatButton mLogin_Google = view.findViewById(R.id.login_google_btn);

    reset.setOnClickListener(this);
    mRegister.setOnClickListener(this);
    mLogin.setOnClickListener(this);
    mLogin_Google.setOnClickListener(this);
    if (BuildConfig.DEBUG) {
      AppCompatButton test = view.findViewById(R.id.test_btn);
      test.setOnClickListener(this);
    }

    loginFormView = view.findViewById(R.id.login_form);
    progressView = view.findViewById(R.id.login_progress);
    return view;
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
    if (Objects.nonNull(googleApiClient)) {
      googleApiClient.stopAutoManage((FragmentActivity) activity);
      googleApiClient.disconnect();
    }
    super.onDestroy();
  }

  /**
   * Attempts to sign in or register the account specified by the login form. If there are form
   * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
   * attempt is made.
   */
  private void attemptLogin(boolean isSignUp) {

    // Reset errors.
    emailView.setError(null);
    passwordView.setError(null);

    // Store values at the date of the login attempt.
    String email = emailView.getText().toString();
    String password = passwordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password, if the user entered one.
    if (TextUtils.isEmpty(password)) {
      passwordView.setError(getString(R.string.error_field_required));
      focusView = passwordView;
      cancel = true;
    } else if (!isPasswordValid(password)) {
      passwordView.setError(getString(R.string.error_invalid_password));
      focusView = passwordView;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      emailView.setError(getString(R.string.error_field_required));
      focusView = emailView;
      cancel = true;
    } else if (!isEmailValid(email)) {
      emailView.setError(getString(R.string.error_invalid_email));
      focusView = emailView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      showProgress(true);
      if (isSignUp) {
        signUp(email, password);

      } else {
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

    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    loginFormView.animate().setDuration(shortAnimTime).alpha(
        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      }
    });

    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    progressView.animate().setDuration(shortAnimTime).alpha(
        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
        showShortSnackBar(view, "Google Sign In failed: " + result.getStatus(),
            SnackBarUtils.TYPE_INFO);
        if (BuildConfig.DEBUG) {
          Log.e(TAG, "onActivityResult: " + result.getStatus());
        }
      }
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.reset_password:
        emailView.setError(null);
        String email = emailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
          InputDialogFragment inputDialogFragment = InputDialogFragment
              .newInstance(getString(R.string.dialog_reset_password),
                  InputDialogFragment.TYPE_EMAIL);
          inputDialogFragment.show(activity.getFragmentManager(), "resetPassword");
        } else {
          AuthUtils.sendRPEmail(MoonlightApplication.getContext(), view, email);
        }
        break;
      case R.id.email_sign_in_button:
        attemptLogin(false);
        break;
      case R.id.email_sign_up_button:
        attemptLogin(true);
        break;
      case R.id.login_google_btn:
        showProgress(true);
        GoogleSignInOptions mGoogleSignInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        if (Objects.isNull(googleApiClient)) {
          googleApiClient = new Builder(activity)
              .enableAutoManage((FragmentActivity) activity,
                  new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                      if (BuildConfig.DEBUG) {
                        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
                      }
                    }
                  }
              )
              .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
              .build();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
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
    showShortSnackBar(view, connectionResult.getErrorMessage(), SnackBarUtils.TYPE_ALERT);
  }

  public void signIn() {
    authStateListener = firebaseAuth -> {
      FirebaseUser user = firebaseAuth.getCurrentUser();
      if (Objects.nonNull(user)) {
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        SPUtils.putString(MoonlightApplication.getContext(),
            "User", "Id", user.getUid());
        User localUser = new User();
        localUser.setUid(user.getUid());
        localUser.setNickname(user.getDisplayName());
        localUser.setEmail(user.getEmail());
        Uri photoUrl = user.getPhotoUrl();
        if (Objects.nonNull(photoUrl)) {
          localUser.setPhotoUrl(photoUrl.toString());
        }
        Task<InstanceIdResult> task = FirebaseInstanceId.getInstance().getInstanceId();
        task.addOnCompleteListener(resultTask -> {
          InstanceIdResult result = resultTask.getResult();
          if (Objects.nonNull(result)) {
            localUser.setToken(result.getToken());
          }
        });
        localUser.setEncryptKey(user.getUid());
        if (isNewUser) {
          commitMoonlight(user.getUid());
        }

        UserUtils.updateUser(user.getUid(), localUser);
        UserUtils.saveUserToCache(MoonlightApplication.getContext(), localUser);
      } else {
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "onAuthStateChanged:signed_out:");
        }
      }
    };
  }

  public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
    }

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(task -> {
          if (BuildConfig.DEBUG) {
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
          }

          if (!task.isSuccessful()) {
            showProgress(false);
            Log.w(TAG, "signInWithCredential", task.getException());
            //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
            String exception = task.getException().getMessage();
            showShortSnackBar(view, "Authentication failed: " + exception,
                SnackBarUtils.TYPE_WARNING);
          } else {
            showProgress(false);
            Intent intent = new Intent(activity, MoonlightActivity.class);
            Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(activity).toBundle();
            activity.startActivity(intent, bundle);
            showShortSnackBar(view, "Google Sign In succeed", SnackBarUtils.TYPE_INFO);
            isNewUser = true;
            SPUtils.putBoolean(MoonlightApplication.getContext(), "User", "google", true);
            activity.finishAfterTransition();
          }
        });
  }

  public void signInWithEmail(final String email, final String password) {
    firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            showProgress(false);
            Intent intent = new Intent(activity, MoonlightActivity.class);
            Bundle bundle = ActivityOptions
                .makeSceneTransitionAnimation(activity).toBundle();
            activity.startActivity(intent, bundle);

            activity.finishAfterTransition();
            if (BuildConfig.DEBUG) {
              Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
            }
          }
        }).addOnFailureListener(e -> {
      showProgress(false);
      showShortSnackBar(view, "Sign In Failed: " + e.toString(),
          SnackBarUtils.TYPE_WARNING);
    });
  }

  public void signUp(final String email, final String password) {

    firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            showProgress(false);
            showLongSnackBar(view, "Sign Up succeed!",
                SnackBarUtils.TYPE_WARNING);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (Objects.nonNull(user)) {
              user.sendEmailVerification()
                  .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                      if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Email sent.");
                      }
                    }
                  });
            }

            signInWithEmail(email, password);
            isNewUser = true;
          } else {
            showProgress(false);
            isNewUser = false;
            //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
            String exception = task.getException().getMessage();
            showLongSnackBar(view, "Sign Up Failed: " + exception,
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
    firebaseAuth.signInAnonymously()
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Intent intent = new Intent(activity, MoonlightActivity.class);
            activity.startActivity(intent);
          }

          showProgress(false);
        });
  }


  public void addListener() {
    firebaseAuth.addAuthStateListener(authStateListener);
  }

  public void removeListener() {
    if (Objects.nonNull(authStateListener)) {
      firebaseAuth.removeAuthStateListener(authStateListener);
    }
  }


  @Subscribe(threadMode = ThreadMode.MAIN)
  public void busAction(BusEvent busEvent) {
    if (busEvent.getMessage().contains("@")) {
      AuthUtils.sendRPEmail(MoonlightApplication.getContext(), view, busEvent.getMessage());
    } else {
      showShortSnackBar(view, "Invalid email address",
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
