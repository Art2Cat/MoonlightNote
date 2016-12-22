package com.art2cat.dev.moonlightnote.Controller.Settings;


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
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivity";
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
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
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
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
                startActivity(new Intent(SettingsActivity.this, MoonlightActivity.class));
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
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || SecurityFragment.class.getName().equals(fragmentName)
                || BackPreferenceFragment.class.getName().equals(fragmentName)
                || AboutPreferenceFragment.class.getName().equals(fragmentName);
    }

    @SuppressLint("ValidFragment")
    public static class SecurityFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
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
                    confirmationDialogFragment.show((getActivity()).getFragmentManager(), null);
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
            GoogleApiClient.OnConnectionFailedListener {
        private static final String CONNECT_TO_DRIVE = "connect_to_drive";
        private static final String BACKUP_TO_DRIVE = "backup_to_drive";
        private static final String RESTORE_FROM_DRIVE = "restore_from_drive";
        private static final String BACKUP_TO_SD = "backup_to_sd";
        private static final String RESTORE_FROM_SD = "restore_from_sd";
        private static final int REQUEST_CODE_CREATOR = 2;
        private static final int REQUEST_CODE_RESOLUTION = 3;
        private static final int REQUEST_CODE_SAVE_TO_DRIVE = 1;
        private GoogleApiClient mGoogleApiClient;
        private FDatabaseUtils mFDatabaseUtils;
        private JSONObject mJsonObject;
        private boolean fileOperation = false;
        private DriveId mFileId;
        public DriveFile file;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_back_up);
            setHasOptionsMenu(true);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mFDatabaseUtils = new FDatabaseUtils(getActivity(), userId);
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
                    fileOperation = true;
                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(driveContentsCallback);
                    break;
                case RESTORE_FROM_DRIVE:
                    fileOperation = false;
                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(driveContentsCallback);
                    break;
                case BACKUP_TO_SD:
                    mFDatabaseUtils.exportNote();
                    SnackBarUtils.longSnackBar(getView(),
                            "Back up succeed! save in internal storage root named Note.json",
                            SnackBarUtils.TYPE_INFO).show();
                    break;
                case RESTORE_FROM_SD:

                    mFDatabaseUtils.restoreAll();
                    SnackBarUtils.longSnackBar(getView(),
                            "Restore succeed!",
                            SnackBarUtils.TYPE_INFO).show();
                    break;
            }
            return false;
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
            }
            // Connect the client. Once connected, the camera is launched.
            mGoogleApiClient.connect();
        }


        /**
         * Create a new file and save it to Drive.
         */
        private void saveFileToDrive() {
            // Start by creating a new contents, and setting a callback.
            JSONObject jsonObject = mJsonObject;
            Log.i(TAG, "Creating new contents.");
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                        @Override
                        public void onResult(DriveApi.DriveContentsResult result) {
                            // If the operation was not successful, we cannot do anything
                            // and must
                            // fail.
                            if (!result.getStatus().isSuccess()) {
                                Log.i(TAG, "Failed to create new contents.");
                                return;
                            }
                            DriveContents driveContents = result.getDriveContents();

                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);
                            try {
                                writer.write("Fuck Google Drive");
                                writer.close();

                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                    .setTitle("Test")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();


                            IntentSender intentSender = Drive.DriveApi
                                    .newCreateFileActivityBuilder()
                                    .setInitialMetadata(metadataChangeSet)
                                    .setInitialDriveContents(result.getDriveContents())
                                    .build(mGoogleApiClient);
                            try {
                                getActivity().startIntentSenderForResult(
                                        intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                Log.i(TAG, "Failed to launch file chooser.");
                            }
                        }
                    });
        }
        public void onClickCreateFile(View view){
            fileOperation = true;

            // create new contents resource
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(driveContentsCallback);

        }

        public void onClickOpenFile(View view){
            fileOperation = false;

            // create new contents resource
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(driveContentsCallback);
        }

        /**
         *  Open list of folder and file of the Google Drive
         */
        public void OpenFileFromGoogleDrive(){

            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[] { "text/plain", "text/html" })
                    .build(mGoogleApiClient);
            try {
                getActivity().startIntentSenderForResult(

                        intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);

            } catch (IntentSender.SendIntentException e) {

                Log.w(TAG, "Unable to send intent", e);
            }

        }


        /**
         * This is Result result handler of Drive contents.
         * this callback method call CreateFileOnGoogleDrive() method
         * and also call OpenFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
         */
        final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
                new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {

                        if (result.getStatus().isSuccess()) {

                            if (fileOperation) {

                                CreateFileOnGoogleDrive(result);

                            } else {

                                OpenFileFromGoogleDrive();

                            }
                        }


                    }
                };

        /**
         * Create a file in root folder using MetadataChangeSet object.
         * @param result
         */
        public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result){


            final DriveContents driveContents = result.getDriveContents();

            // Perform I/O off the UI thread.
            new Thread() {
                @Override
                public void run() {
                    // write content to DriveContents
                    OutputStream outputStream = driveContents.getOutputStream();
                    Writer writer = new OutputStreamWriter(outputStream);
                    try {
                        writer.write("Hello abhay!");
                        writer.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("abhaytest2")
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    // create a file in root folder
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, driveContents)
                            .setResultCallback(fileCallback);
                }
            }.start();
        }

        /**
         * Handle result of Created file
         */
        final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
                ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(DriveFolder.DriveFileResult result) {
                        if (result.getStatus().isSuccess()) {

                            Toast.makeText(getActivity(), "file created: "+""+
                                    result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();

                        }

                        return;

                    }
                };


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQUEST_CODE_CREATOR:
                    if (resultCode == Activity.RESULT_OK) {
                        //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
//                                REQUEST_CODE_SAVE_TO_DRIVE);
                        Utils.showToast(getActivity(), "Done", 1);
                    }
                    break;
                case REQUEST_CODE_SAVE_TO_DRIVE:
                    if (resultCode == Activity.RESULT_OK) {
                        // Store the image data as a bitmap for writing later.
                        mJsonObject = (JSONObject) data.getExtras().get("data");
                    }
                    break;
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            // Called whenever the API client fails to connect.
            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                // show the localized error dialog.
                GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
                return;
            }
            // The failure has a resolution. Resolve it.
            // Called typically when the app is not yet authorized, and an
            // authorization
            // dialog is displayed to the user.
            try {
                result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
            }
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.i(TAG, "API client connected.");
            SnackBarUtils.shortSnackBar(getView(), "Google Drive connected!", SnackBarUtils.TYPE_INFO).show();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.i(TAG, "GoogleApiClient connection suspended");
        }

    }

    @SuppressLint("ValidFragment")
    public static class AboutPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
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
            switch (preference.getKey()) {
                case POLICY:
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_POLICY);
                    startActivity(intent);
                    break;
                case LICENSE:
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_LICENSE);
                    startActivity(intent);
                    break;
                case ABOUT:
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_ABOUT);
                    startActivity(intent);
                    break;
            }

            return false;
        }
    }

}
