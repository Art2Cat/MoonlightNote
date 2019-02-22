package com.art2cat.dev.moonlightnote.controller.settings;


import static com.art2cat.dev.moonlightnote.constants.Constants.STORAGE_PERMS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.Toast;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment;
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.model.NoteLab;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.ToastUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    if (actionBar != null) {
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
                  Constants.EXTRA_TYPE_CDF_DISABLE_SECURITY);
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
      Preference.OnPreferenceClickListener, GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks {

    private static final String CONNECT_TO_DRIVE = "connect_to_drive";
    private static final String BACKUP_TO_DRIVE = "backup_to_drive";
    private static final String RESTORE_FROM_DRIVE = "restore_from_drive";
    private static final String BACKUP_TO_SD = "backup_to_sd";
    private static final String RESTORE_FROM_SD = "restore_from_sd";
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CODE_SAVE_TO_DRIVE = 1;
    /**
     * Handle result of Created file
     */
    public DriveFile file;
    private GoogleApiClient mGoogleApiClient;
    private FDatabaseUtils mFDatabaseUtils;
    private String mData;
    private int mType;
    private CircleProgressDialogFragment mCircleProgressDialogFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_back_up);
      setHasOptionsMenu(true);
      String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
      mFDatabaseUtils = FDatabaseUtils.newInstance(getActivity(), userId);
      mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance();
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
          connectToDrive();
          break;
        case BACKUP_TO_DRIVE:
          if (mGoogleApiClient != null) {
            mFDatabaseUtils.exportNote(1);
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsResult -> {
                  if (driveContentsResult.getStatus().isSuccess()) {
                    CreateFileOnGoogleDrive(driveContentsResult);
                  }
                });
          } else {
            SnackBarUtils.shortSnackBar(getView(),
                "please connect to Google Drive first!",
                SnackBarUtils.TYPE_INFO).show();
          }
          break;
        case RESTORE_FROM_DRIVE:
          if (mGoogleApiClient != null) {
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsResult -> {
                  if (driveContentsResult.getStatus().isSuccess()) {
                    queryFile();
                  }
                });
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
          mFDatabaseUtils.exportNote(0);
        } else {
          mFDatabaseUtils.restoreAll();
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
            mFDatabaseUtils.exportNote(0);
            break;
          case 1:
            mFDatabaseUtils.restoreAll();
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
      if (mGoogleApiClient != null) {
        mGoogleApiClient.disconnect();
      }
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      mFDatabaseUtils.removeListener();
      if (!EXECUTOR_SERVICE.isTerminated()) {
        EXECUTOR_SERVICE.shutdown();
      }
    }

    private void connectToDrive() {

      if (mGoogleApiClient == null) {
        // Create the API client and bind it to an instance variable.
        // We use this instance as the callback for connection and connection
        // failures.
        // Since no account name is passed, the user is prompted to choose.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
            .addApi(Drive.API)
            .addScope(Drive.SCOPE_FILE)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
        mCircleProgressDialogFragment.show(getFragmentManager(), "progressbar");
      }
      // Connect the client. Once connected, the camera is launched.
      mGoogleApiClient.connect();
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void readFileFromDrive(DriveId id) {
      final DriveFile f = id.asDriveFile();
      EXECUTOR_SERVICE.execute(() -> {
        DriveApi.DriveContentsResult driveContentsResult =
            f.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY,
                null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
          if (BuildConfig.DEBUG) {
            Log.d(TAG, "readFileFromDrive: " + driveContentsResult.getStatus());
          }
          return;
        }

        DriveContents driveContents = driveContentsResult.getDriveContents();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(driveContents.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
          while ((line = reader.readLine()) != null) {
            builder.append(line);
          }
          String contentsAsString = builder.toString();
          Gson gson = new Gson();
          NoteLab noteLab = gson.fromJson(contentsAsString, NoteLab.class);
          if (noteLab == null) {
            return;
          }
          if (BuildConfig.DEBUG) {
            Log.i(TAG, "readFileFromDrive: " + noteLab.getMoonlights().size());
          }
          mFDatabaseUtils.restoreAll(noteLab);
        } catch (IOException e) {
          Log.e(TAG, "readFileFromDrive: IOException while reading from the stream, ", e);
        }

        driveContents.discard(mGoogleApiClient);
      });

    }

    private void queryFile() {
      Query query = new Query.Builder()
          .addFilter(Filters.contains(SearchableField.TITLE, "Note.json"))
          .build();
      Drive.DriveApi.query(
          mGoogleApiClient, query).setResultCallback(result -> {
        if (!result.getStatus().isSuccess()) {
          if (BuildConfig.DEBUG) {
            Log.d(TAG, "Problem while retrieving results");
          }
          return;
        }

        Metadata metadata = result.getMetadataBuffer().get(0);
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "metadata.getDriveId():" + metadata.getDriveId());
        }
        readFileFromDrive(metadata.getDriveId());
      });
    }

    /**
     * Create a file in root folder using MetadataChangeSet object.
     */
    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result) {

      final DriveContents driveContents = result.getDriveContents();

      mData = mFDatabaseUtils.getJson();
      EXECUTOR_SERVICE.execute(() -> {
        OutputStream outputStream = driveContents.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream);

        if (mData == null) {
          mData = mFDatabaseUtils.getJson();
        }
        if (mData != null) {
          try {
            writer.write(mData);
            writer.close();
          } catch (IOException e) {
            Log.e(TAG, "CreateFileOnGoogleDrive: ", e);
          }
        }
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
            .setTitle("Note.json")
            .setMimeType("plain/text")
            .setStarred(true).build();

        // create a file in root folder
        Drive.DriveApi.getRootFolder(mGoogleApiClient)
            .createFile(mGoogleApiClient, changeSet, driveContents)
            .setResultCallback(result1 -> {
              if (result1.getStatus().isSuccess()) {

                Toast.makeText(getActivity(), "file created: " + "" +
                        result1.getDriveFile().getDriveId(),
                    Toast.LENGTH_LONG).show();
                SPUtils.putString(getActivity(), "User",
                    "GoogleDriveFileID",
                    result1.getDriveFile().getDriveId().toString());
              }
            });
      });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      switch (requestCode) {
        case REQUEST_CODE_CREATOR:
          ToastUtils.with(getActivity()).setMessage("Done").showShortToast();
          if (resultCode == Activity.RESULT_OK) {
            //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
//                                REQUEST_CODE_SAVE_TO_DRIVE);
            ToastUtils.with(getActivity()).setMessage("Done").showShortToast();
          }
          break;
        case REQUEST_CODE_SAVE_TO_DRIVE:
//                    if (resultCode == Activity.RESULT_OK) {
//                        // Store the image data as a bitmap for writing later.
//                    }
          break;
      }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
      mCircleProgressDialogFragment.dismiss();
      // Called whenever the API client fails to connect.

      Log.w(TAG, "onConnectionFailed: " + result.getErrorMessage());
      if (!result.hasResolution()) {
        // show the localized error dialog.
        GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0)
            .show();
        return;
      }
      // The failure has a resolution. Resolve it.
      // Called typically when the app is not yet authorized, and an
      // authorization
      // dialog is displayed to the user.
      try {
        result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
      } catch (IntentSender.SendIntentException e) {
        Log.e(TAG, "onConnectionFailed: Exception while starting resolution activity, ", e);
      }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
      mCircleProgressDialogFragment.dismiss();
      if (BuildConfig.DEBUG) {
        Log.i(TAG, "onConnected: API client connected.");
      }
      SnackBarUtils.shortSnackBar(getView(), "Google Drive connected!",
          SnackBarUtils.TYPE_INFO).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
      mCircleProgressDialogFragment.dismiss();
      Log.i(TAG, "onConnectionSuspended: " + "GoogleApiClient connection suspended");
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