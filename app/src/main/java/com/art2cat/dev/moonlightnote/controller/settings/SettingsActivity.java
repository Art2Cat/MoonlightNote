package com.art2cat.dev.moonlightnote.controller.settings;


import static com.art2cat.dev.moonlightnote.constants.Constants.STORAGE_PERMS;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.model.NoteLab;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.ToastUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SettingsActivity extends AppCompatPreferenceActivity {

  private static final String TAG = "SettingsActivity";
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
  /**
   * A preference value change listener that updates the preference's summary to reflect its new
   * value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
      (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
          // For list preferences, look up the correct display value in
          // the preference's 'entries' list.
          ListPreference listPreference = (ListPreference) preference;
          int index = listPreference.findIndexOfValue(stringValue);

          // Set the summary to reflect the new value.
          preference.setSummary(
              index >= 0
                  ? listPreference.getEntries()[index]
                  : null);

        } else {
          // For all other preferences, set the summary to the value's
          // simple string representation.
          preference.setSummary(stringValue);
        }
        return true;
      };

  /**
   * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
   * are extra-large.
   */
  private static boolean isXLargeTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (Objects.nonNull(actionBar)) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      if (!super.onMenuItemSelected(featureId, item)) {
        NavUtils.navigateUpFromSameTask(this);
      }
      if (super.onMenuItemSelected(featureId, item) && isXLargeTablet(this)) {
        startActivity(new Intent(this, MoonlightActivity.class));
        this.finish();
      }
      return true;
    }
    return super.onMenuItemSelected(featureId, item);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onIsMultiPane() {
    return isXLargeTablet(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * This method stops fragment injection in malicious applications. Make sure to deny any unknown
   * fragments here.
   */
  protected boolean isValidFragment(String fragmentName) {
    return PreferenceFragment.class.getName().equals(fragmentName)
        || SecurityFragment.class.getName().equals(fragmentName)
        || BackPreferenceFragment.class.getName().equals(fragmentName)
        || AboutPreferenceFragment.class.getName().equals(fragmentName);
  }

  @SuppressLint("ValidFragment")
  public static class SecurityFragment extends PreferenceFragment
      implements Preference.OnPreferenceClickListener {

    public static final String PROTECTION = "enable_protection";
    public static final String PIN = "enable_pin";
    public static final String PATTERN = "enable_pattern";
    private static final int REQUEST_ENABLE_PIN = 11;
    private static final int REQUEST_ENABLE_PATTERN = 12;
    private Preference mProtection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_security);
      setHasOptionsMenu(true);
      mProtection = findPreference(PROTECTION);
      int type = SPUtils.getInt(getActivity(),
          Constants.USER_CONFIG,
          Constants.USER_CONFIG_SECURITY_ENABLE,
          0);
      if (type != 0) {
        mProtection.setEnabled(true);
      } else {
        mProtection.setEnabled(false);
      }
      Preference pin = findPreference(PIN);
      Preference pattern = findPreference(PATTERN);
      mProtection.setOnPreferenceClickListener(this);
      pin.setOnPreferenceClickListener(this);
      pattern.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
      switch (preference.getKey()) {
        case PROTECTION:
          ConfirmationDialogFragment confirmationDialogFragment =
              ConfirmationDialogFragment.newInstance(
                  getActivity().getString(R.string.confirmation_title),
                  getActivity().getString(R.string.confirmation_disable_security),
                  ConfirmationDialogFragment.TYPE_DISABLE_SECURITY);
          confirmationDialogFragment.show(getActivity().getFragmentManager(), "cf");
          break;
        case PIN:
          Intent pin = new Intent(getActivity(), MoonlightPinActivity.class);
          pin.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
          startActivityForResult(pin, REQUEST_ENABLE_PIN);
          SPUtils.putInt(getActivity(),
              Constants.USER_CONFIG,
              Constants.USER_CONFIG_SECURITY_ENABLE,
              Constants.EXTRA_PIN);
          mProtection.setEnabled(true);
          break;
        case PATTERN:
          mProtection.setEnabled(true);
          break;
      }
      return false;
    }
  }

  @SuppressLint("ValidFragment")
  public static class BackPreferenceFragment extends PreferenceFragment implements
      Preference.OnPreferenceClickListener, EasyPermissions.PermissionCallbacks {

    private static final String CONNECT_TO_DRIVE = "connect_to_drive";
    private static final String BACKUP_TO_DRIVE = "backup_to_drive";
    private static final String RESTORE_FROM_DRIVE = "restore_from_drive";
    private static final String BACKUP_TO_SD = "backup_to_sd";
    private static final String RESTORE_FROM_SD = "restore_from_sd";
    private static final String DRIVE_SCOPE = "https://www.googleapis.com/auth/drive";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    // Request codes
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_RECOVERABLE = 9002;
    /**
     * Handle result of Created file
     */
    String userId;
    private GoogleSignInClient mGoogleSignInClient;
    private Account mAccount;
    private int mType;
    private CircleProgressDialogFragment mCircleProgressDialogFragment;
    private DatabaseReference exportDatabaseReference;
    private ValueEventListener exportValueEventListener;
    private String key;
    private NoteLab noteLab;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static final String FILE_MIME_TYPE = "plain/text";
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_back_up);
      setHasOptionsMenu(true);
      userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
      mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance();
      exportDatabaseReference = FirebaseDatabase.getInstance().getReference()
          .child("users-moonlight").child(userId).child("note");
      key = SPUtils.getString(getContext(),
          "User", "EncryptKey", null);
      // Configure sign-in to request the user's ID, email address, basic profile,
      // and readonly access to contacts.
      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestScopes(new Scope(DRIVE_SCOPE))
          .requestEmail()
          .build();

      mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
      Preference connectToDrive = findPreference(CONNECT_TO_DRIVE);
      Preference backupToDrive = findPreference(BACKUP_TO_DRIVE);
      Preference restoreFromDrive = findPreference(RESTORE_FROM_DRIVE);
      Preference backupToSd = findPreference(BACKUP_TO_SD);
      Preference restoreFromSd = findPreference(RESTORE_FROM_SD);
      connectToDrive.setOnPreferenceClickListener(this);
      backupToDrive.setOnPreferenceClickListener(this);
      restoreFromDrive.setOnPreferenceClickListener(this);
      backupToSd.setOnPreferenceClickListener(this);
      restoreFromSd.setOnPreferenceClickListener(this);
    }

    @Override
    public void onStart() {
      super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
      switch (preference.getKey()) {
        case CONNECT_TO_DRIVE:
          Intent signInIntent = mGoogleSignInClient.getSignInIntent();
          startActivityForResult(signInIntent, RC_SIGN_IN);
          break;
        case BACKUP_TO_DRIVE:
          if (Objects.nonNull(mAccount)) {
            backup(mAccount);
          } else {
            SnackBarUtils.shortSnackBar(getView(),
                "please connect to Google Drive first!",
                SnackBarUtils.TYPE_INFO).show();
          }
          break;
        case RESTORE_FROM_DRIVE:

          if (Objects.nonNull(mAccount)) {
            readFileFromDrive(mAccount);
          } else {
            SnackBarUtils.shortSnackBar(getView(),
                "please connect to Google Drive first!",
                SnackBarUtils.TYPE_INFO).show();
          }
          break;
        case BACKUP_TO_SD:
          mType = 0;
          requestPermission(0);
          break;
        case RESTORE_FROM_SD:
          mType = 1;
          requestPermission(1);
          break;
      }
      return false;
    }

    private void backup(Account account) {

      noteLab = new NoteLab();
      if (Objects.isNull(exportValueEventListener)) {
        exportValueEventListener = new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (Objects.nonNull(dataSnapshot)) {

              Log.i(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
              int size = (int) dataSnapshot.getChildrenCount();
              dataSnapshot.getChildren().forEach(child -> {
                Moonlight moonlight = child.getValue(Moonlight.class);
                noteLab.setMoonlight(MoonlightEncryptUtils.decryptMoonlight(key, moonlight));
              });

              if (noteLab.getMoonlights().size() == size) {
                // if account is null, backup to local.
                if (Objects.isNull(account)) {
                  Utils.saveNoteToLocal(noteLab);
                  ToastUtils.with(getContext())
                      .setMessage("Back up succeed! save in internal storage root name Note.json")
                      .showShortToast();
                  BusEventUtils.post(Constants.BUS_FLAG_EXPORT_DATA_DONE, null);
                } else {
                  createFileOnGoogleDrive(noteLab, account);
                }
              }
            } else {
              Log.e(TAG, "onDataChange: fail");
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
          }

        };

        if (Objects.nonNull(exportDatabaseReference)) {
          exportDatabaseReference.addValueEventListener(exportValueEventListener);
        }
      }

    }

    @AfterPermissionGranted(STORAGE_PERMS)
    private void requestPermission(int type) {
      if (!EasyPermissions.hasPermissions(getActivity(),
          Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        EasyPermissions.requestPermissions(getActivity(),
            "If you want to do this continue, " +
                "you should give App storage permission ",
            STORAGE_PERMS, Manifest.permission.WRITE_EXTERNAL_STORAGE);
      } else {
        if (type == 0) {
          backup(null);
        } else {
          noteLab = Utils.getNoteFromLocal();
          FDatabaseUtils.restoreAll(getContext(), userId, noteLab);
          ToastUtils.with(getContext())
              .setMessage("Restore succeed!")
              .showLongToast();
        }
      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      // EasyPermissions handles the request result.
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
      if (requestCode == STORAGE_PERMS) {
        Log.d(TAG, "onPermissionsGranted: ");
        switch (mType) {
          case 0:
            backup(null);
            break;
          case 1:
            NoteLab noteLab = Utils.getNoteFromLocal();
            FDatabaseUtils.restoreAll(getContext(), userId, noteLab);
            Toast.makeText(getContext(), "Restore succeed!",
                Toast.LENGTH_LONG).show();
            break;
        }
      }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
      if (requestCode == STORAGE_PERMS) {
        SnackBarUtils.shortSnackBar(getView(), "This action need Storage permission",
            SnackBarUtils.TYPE_INFO).show();
      }
    }

    @Override
    public void onResume() {
      super.onResume();
    }

    @Override
    public void onPause() {
      super.onPause();
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      if (!EXECUTOR_SERVICE.isTerminated()) {
        EXECUTOR_SERVICE.shutdown();
      }
      if (Objects.nonNull(mGoogleSignInClient)) {
        mGoogleSignInClient.signOut();
      }

      if (Objects.nonNull(exportDatabaseReference) && Objects.nonNull(exportValueEventListener)) {
        exportDatabaseReference.removeEventListener(exportValueEventListener);
      }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
      Log.d(TAG, "handleSignInResult:" + completedTask.isSuccessful());

      try {
        GoogleSignInAccount account = completedTask.getResult(ApiException.class);

        // Store the account from the result
        if (Objects.nonNull(account)) {
          mAccount = account.getAccount();
          mCircleProgressDialogFragment.dismiss();
        }

      } catch (ApiException e) {
        Log.w(TAG, "handleSignInResult:error", e);

        mCircleProgressDialogFragment.dismiss();
        // Clear the local account
        mAccount = null;

        // Signed out, show unauthenticated UI.
      }
    }


    /**
     * Create a new file and save it to Drive.
     */
    private void readFileFromDrive(Account account) {

      Future<Boolean> future = EXECUTOR_SERVICE.submit(() -> {
        Context context = getActivity().getApplicationContext();
        try {
          GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
              context,
              Collections.singleton(DRIVE_SCOPE));
          credential.setSelectedAccount(account);
          Drive drive = new com.google.api.services.drive.Drive.Builder(
              HTTP_TRANSPORT, JSON_FACTORY, credential)
//              .setApplicationName(APPLICATION_NAME)
              .build();
          String pageToken = null;
          do {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(".json");
            FileList result = drive.files().list()
                .setQ("mimeType='text/json'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute();
            for (File file : result.getFiles()) {
              System.out.printf("Found file: %s (%s)\n",
                  file.getName(), file.getId());
              if ("Note.json".equals(file.getName())) {
                InputStream inputStream = drive.files().export(file.getId(), FILE_MIME_TYPE)
                    .executeMediaAsInputStream();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                  while ((line = reader.readLine()) != null) {
                    builder.append(line);
                  }
                  String contentsAsString = builder.toString();
                  Gson gson = new Gson();
                  NoteLab noteLab = gson.fromJson(contentsAsString, NoteLab.class);
                  if (Objects.isNull(noteLab)) {
                    return false;
                  }
                  if (BuildConfig.DEBUG) {
                    Log.i(TAG, "readFileFromDrive: " + noteLab.getMoonlights().size());
                  }
                  FDatabaseUtils.restoreAll(getContext(), userId, noteLab);
                  return true;
                } catch (IOException e) {
                  Log.e(TAG, "readFileFromDrive: IOException while reading from the stream, ", e);
                  return false;
                }
              }
            }
            pageToken = result.getNextPageToken();
          } while (pageToken != null);


        } catch (UserRecoverableAuthIOException recoverableException) {
//          getActivity().getApplicationContext().onRecoverableAuthException(recoverableException);
        } catch (IOException e) {
          Log.w(TAG, "getContacts:exception", e);
          return false;
        }
        return false;
      });

      if (future.isDone()) {
        try {
          if (Boolean.TRUE.equals(future.get())) {
            Toast.makeText(getContext(), "Restore succeed!",
                Toast.LENGTH_LONG).show();
          }
        } catch (ExecutionException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    public void createFileOnGoogleDrive(NoteLab noteLab, Account account) {

      Context context = getActivity().getApplicationContext();
      EXECUTOR_SERVICE.execute(() -> {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
            context,
            Collections.singleton(DRIVE_SCOPE));
        credential.setSelectedAccount(account);
        Drive drive = new com.google.api.services.drive.Drive.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, credential)
//              .setApplicationName(APPLICATION_NAME)
            .build();
        File fileMetadata = new File();

        fileMetadata
            .setName("Note.json")
            .setMimeType(FILE_MIME_TYPE)
            .setStarred(true);
        try {
          java.io.File filePath = new java.io.File("/data/local/tmp/Note.json");
          filePath.createNewFile();
          Gson gson = new Gson();
          String mJson = gson.toJson(noteLab);
          BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.getAbsolutePath()));
          writer.write(mJson);

          FileContent mediaContent = new FileContent(FILE_MIME_TYPE, filePath);
          File file = drive.files().create(fileMetadata, mediaContent)
              .setFields("id")
              .execute();
          if (Objects.nonNull(file) && Objects.nonNull(file.getId())) {
            Toast.makeText(context, "file created: " + "" +
                    file.getId(),
                Toast.LENGTH_LONG).show();
            SPUtils.putString(context, "User",
                "GoogleDriveFileID",
                file.getId());
            writer.close();
            if (filePath.exists()) {
              filePath.delete();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      switch (requestCode) {
        case REQUEST_CODE_CREATOR:
          if (resultCode == Activity.RESULT_OK) {
            ToastUtils.with(getActivity()).setMessage("Done").showShortToast();
          }
          break;
        case RC_SIGN_IN:
          mCircleProgressDialogFragment.show(getFragmentManager(), "progress");
          Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
          handleSignInResult(task);
          break;

        // Handling a user-recoverable auth exception
        case RC_RECOVERABLE:
          if (resultCode == RESULT_OK) {
            // TODO: ????
          }
          break;
      }
    }


  }

  @SuppressLint("ValidFragment")
  public static class AboutPreferenceFragment extends PreferenceFragment
      implements Preference.OnPreferenceClickListener {

    private static final String POLICY = "settings_policy";
    private static final String LICENSE = "settings_license";
    private static final String ABOUT = "settings_about";

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_about);
      setHasOptionsMenu(true);
      Preference policy = findPreference(POLICY);
      Preference license = findPreference(LICENSE);
      Preference about = findPreference(ABOUT);
      policy.setOnPreferenceClickListener(this);
      license.setOnPreferenceClickListener(this);
      about.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
      Intent intent = new Intent(getActivity(), SettingsSecondActivity.class);
      String settingsTypeEnum = SettingsTypeEnum.class.getSimpleName();
      switch (preference.getKey()) {
        case POLICY:
          intent.putExtra(settingsTypeEnum, SettingsTypeEnum.POLICY);
          startActivity(intent);
          break;
        case LICENSE:
          intent.putExtra(settingsTypeEnum, SettingsTypeEnum.LICENSE);
          startActivity(intent);
          break;
        case ABOUT:
          intent.putExtra(settingsTypeEnum, SettingsTypeEnum.ABOUT);
          startActivity(intent);
          break;
      }
      return false;
    }
  }
}