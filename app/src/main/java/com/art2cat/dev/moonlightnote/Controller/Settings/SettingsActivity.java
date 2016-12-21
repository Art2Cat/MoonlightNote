package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;

import com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment.ConfirmationDialogFragment;
import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseAuth;

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
    public static class BackPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        private static final String BACKUP_TO_DRIVE = "backup_to_drive";
        private static final String RESTORE_FROM_DRIVE = "restore_from_drive";
        private static final String BACKUP_TO_SD = "backup_to_sd";
        private static final String RESTORE_FROM_SD = "restore_from_sd";
        private FDatabaseUtils mFDatabaseUtils;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_back_up);
            setHasOptionsMenu(true);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mFDatabaseUtils = new FDatabaseUtils(getActivity(), userId);
            Preference backupToDrive = findPreference(BACKUP_TO_DRIVE);
            Preference restoreFromDrive = findPreference(RESTORE_FROM_DRIVE);
            Preference backupToSd = findPreference(BACKUP_TO_SD);
            Preference restoreFromSd = findPreference(RESTORE_FROM_SD);
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
                case BACKUP_TO_DRIVE:
                break;
                case RESTORE_FROM_DRIVE:
                break;
                case BACKUP_TO_SD:
                    mFDatabaseUtils.exportNote();
                break;
                case RESTORE_FROM_SD:
                    mFDatabaseUtils.restoreAll();
                break;
            }
            return false;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mFDatabaseUtils.removeListener();
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
