package com.art2cat.dev.moonlightnote.controller.settings

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.model.Constants.Companion.STORAGE_PERMS
import com.art2cat.dev.moonlightnote.model.NoteLab
import com.art2cat.dev.moonlightnote.utils.*
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.github.orangegangsters.lollipin.lib.managers.AppLock
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * Created by Rorschach
 * on 24/05/2017 8:38 PM.
 */
/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
   * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
   * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            if (super.onMenuItemSelected(featureId, item) && isXLargeTablet(this)) {
                startActivity(Intent(this@SettingsActivity, MoonlightActivity::class.java))
                this.finish()
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || SecurityFragment::class.java.name == fragmentName
                || BackPreferenceFragment::class.java.name == fragmentName
                || AboutPreferenceFragment::class.java.name == fragmentName
    }

    @SuppressLint("ValidFragment")
    class SecurityFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {
        private var mProtection: Preference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_security)
            setHasOptionsMenu(true)
            mProtection = findPreference(PROTECTION)
            val type = SPUtils.getInt(activity,
                    Constants.USER_CONFIG,
                    Constants.USER_CONFIG_SECURITY_ENABLE,
                    0)
            if (type != 0) {
                mProtection!!.isEnabled = true
            } else {
                mProtection!!.isEnabled = false
            }
            val pin = findPreference(PIN)
            val pattern = findPreference(PATTERN)
            mProtection!!.onPreferenceClickListener = this
            pin.onPreferenceClickListener = this
            pattern.onPreferenceClickListener = this
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            when (preference.key) {
                PROTECTION -> {
                    val confirmationDialogFragment = ConfirmationDialogFragment.newInstance(
                            activity.getString(R.string.confirmation_title),
                            activity.getString(R.string.confirmation_disable_security),
                            Constants.EXTRA_TYPE_CDF_DISABLE_SECURITY)
                    confirmationDialogFragment.show(activity.fragmentManager, "cf")
                }
                PIN -> {
                    val pin = Intent(activity, MoonlightPinActivity::class.java)
                    pin.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK)
                    startActivityForResult(pin, REQUEST_ENABLE_PIN)
                    SPUtils.putInt(activity,
                            Constants.USER_CONFIG,
                            Constants.USER_CONFIG_SECURITY_ENABLE,
                            Constants.EXTRA_PIN)
                    mProtection!!.isEnabled = true
                }
                PATTERN -> mProtection!!.isEnabled = true
            }
            return false
        }

        companion object {
            val PROTECTION = "enable_protection"
            val PIN = "enable_pin"
            val PATTERN = "enable_pattern"
            private val REQUEST_ENABLE_PIN = 11
            private val REQUEST_ENABLE_PATTERN = 12
        }
    }

    @SuppressLint("ValidFragment")
    class BackPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks {
        /**
         * Handle result of Created file
         */
        private val fileCallback = ResultCallback<DriveFolder.DriveFileResult> { result ->
            if (result.status.isSuccess) {

                Toast.makeText(activity, "file created: " + "" +
                        result.driveFile.driveId, Toast.LENGTH_LONG).show()
                result.driveFile.driveId.toString()
                SPUtils.putString(activity, "User",
                        "GoogleDriveFileID", result.driveFile.driveId.toString())

            }
        }
        var file: DriveFile? = null
        private var mGoogleApiClient: GoogleApiClient? = null
        private var mFDatabaseUtils: FDatabaseUtils? = null
        private val metadataCallback = ResultCallback<DriveApi.MetadataBufferResult> { result ->
            if (!result.status.isSuccess) {
                Log.d(TAG, "Problem while retrieving results")
                return@ResultCallback
            }

            val metadata = result.metadataBuffer.get(0)
            if (BuildConfig.DEBUG)
                Log.d(TAG, "metadata.getDriveId():" + metadata.driveId)
            readFileFromDrive(metadata.driveId)
        }
        private var mData: String? = null
        private var mType: Int = 0
        private var fileOperation = false
        private var mCircleProgressDialogFragment: CircleProgressDialogFragment? = null

        /**
         * This is Result result handler of Drive contents.
         * this callback method call CreateFileOnGoogleDrive() method
         * and also call readFileFromGoogleDrive() method, send intent onActivityResult() method to handle result.
         */
        private val driveContentsCallback = ResultCallback<DriveApi.DriveContentsResult> { result ->
            if (result.status.isSuccess) {

                if (fileOperation) {
                    CreateFileOnGoogleDrive(result)
                } else {
                    queryFile()
                }
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_back_up)
            setHasOptionsMenu(true)
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            mFDatabaseUtils = FDatabaseUtils.newInstance(activity, userId)
            mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance()
            val connectToDrive = findPreference(CONNECT_TO_DRIVE)
            val backupToDrive = findPreference(BACKUP_TO_DRIVE)
            val restoreFromDrive = findPreference(RESTORE_FROM_DRIVE)
            val backupToSd = findPreference(BACKUP_TO_SD)
            val restoreFromSd = findPreference(RESTORE_FROM_SD)
            connectToDrive.onPreferenceClickListener = this
            backupToDrive.onPreferenceClickListener = this
            restoreFromDrive.onPreferenceClickListener = this
            backupToSd.onPreferenceClickListener = this
            restoreFromSd.onPreferenceClickListener = this
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            when (preference.key) {
                CONNECT_TO_DRIVE -> connectToDrive()
                BACKUP_TO_DRIVE -> if (mGoogleApiClient != null) {
                    fileOperation = true
                    mFDatabaseUtils!!.exportNote(1)
                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(driveContentsCallback)
                } else {
                    SnackBarUtils.shortSnackBar(view,
                            "please connect to Google Drive first!",
                            SnackBarUtils.TYPE_INFO).show()
                }
                RESTORE_FROM_DRIVE -> if (mGoogleApiClient != null) {
                    fileOperation = false
                    Drive.DriveApi.newDriveContents(mGoogleApiClient)
                            .setResultCallback(driveContentsCallback)
                } else {
                    SnackBarUtils.shortSnackBar(view,
                            "please connect to Google Drive first!",
                            SnackBarUtils.TYPE_INFO).show()
                }
                BACKUP_TO_SD -> {
                    mType = 0
                    requestPermission(0)
                }
                RESTORE_FROM_SD -> {
                    mType = 1
                    requestPermission(1)
                }
            }
            return false
        }

        @AfterPermissionGranted(STORAGE_PERMS)
        private fun requestPermission(type: Int) {
            val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (!EasyPermissions.hasPermissions(activity, perm)) {
                PermissionUtils.requestStorage(activity, perm)
            } else {
                if (type == 0) {
                    mFDatabaseUtils!!.exportNote(0)
                } else {
                    mFDatabaseUtils!!.restoreAll()
                }
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            // EasyPermissions handles the request result.
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        }

        override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
            if (requestCode == STORAGE_PERMS) {
                Log.d(TAG, "onPermissionsGranted: ")
                when (mType) {
                    0 -> mFDatabaseUtils!!.exportNote(0)
                    1 -> mFDatabaseUtils!!.restoreAll()
                }
            }
        }

        override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
            if (requestCode == STORAGE_PERMS) {
                SnackBarUtils.shortSnackBar(view, "This action need Storage permission",
                        SnackBarUtils.TYPE_INFO).show()
            }
        }

        override fun onResume() {
            super.onResume()
        }

        override fun onPause() {
            super.onPause()
            if (mGoogleApiClient != null) {
                mGoogleApiClient!!.disconnect()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            mFDatabaseUtils!!.removeListener()
        }

        private fun connectToDrive() {

            if (mGoogleApiClient == null) {
                // Create the API client and bind it to an instance variable.
                // We use this instance as the callback for connection and connection
                // failures.
                // Since no account name is passed, the user is prompted to choose.
                mGoogleApiClient = GoogleApiClient.Builder(activity)
                        .addApi(Drive.API)
                        .addScope(Drive.SCOPE_FILE)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build()
                mCircleProgressDialogFragment!!.show(fragmentManager, "progressbar")
            }
            // Connect the client. Once connected, the camera is launched.
            mGoogleApiClient!!.connect()
        }

        /**
         * Create a new file and save it to Drive.
         */
        private fun readFileFromDrive(id: DriveId) {
            val f = id.asDriveFile()
            Thread(Runnable {
                val driveContentsResult = f.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await()
                if (!driveContentsResult.status.isSuccess) {
                    LogUtils.getInstance(TAG).setMessage("run: " + driveContentsResult.status).debug()
                    return@Runnable
                }

                val driveContents = driveContentsResult.driveContents
                val reader = BufferedReader(
                        InputStreamReader(driveContents.inputStream))
                val builder = StringBuilder()
                var line: String
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line)
                    }
                    val contentsAsString = builder.toString()
                    LogUtils.getInstance(TAG).setMessage(contentsAsString).debug()
                    val gson = Gson()
                    val noteLab = gson.fromJson(contentsAsString, NoteLab::class.java) ?: return@Runnable
                    LogUtils.getInstance(TAG).setMessage("onResult: " + noteLab.moonlights.size).debug()
                    mFDatabaseUtils!!.restoreAll(noteLab)
                } catch (e: IOException) {
                    LogUtils.getInstance(TAG).setMessage("IOException while reading from the stream").error(e)
                }

                driveContents.discard(mGoogleApiClient)
            }).start()
        }

        private fun queryFile() {
            val query = Query.Builder()
                    .addFilter(Filters.contains(SearchableField.TITLE, "Note.json"))
                    .build()
            Drive.DriveApi.query(
                    mGoogleApiClient, query).setResultCallback(metadataCallback)
        }

        /**
         * Create a file in root folder using MetadataChangeSet object.

         * @param result
         */
        fun CreateFileOnGoogleDrive(result: DriveApi.DriveContentsResult) {

            val driveContents = result.driveContents

            mData = mFDatabaseUtils!!.json

            // Perform I/O off the UI thread.
            object : Thread() {
                override fun run() {
                    val outputStream = driveContents.outputStream
                    val writer = OutputStreamWriter(outputStream)

                    if (mData == null) {
                        mData = mFDatabaseUtils!!.json
                    }
                    if (mData != null) {
                        try {
                            writer.write(mData!!)
                            writer.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                    val changeSet = MetadataChangeSet.Builder()
                            .setTitle("Note.json")
                            .setMimeType("plain/text")
                            .setStarred(true).build()

                    // create a file in root folder
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, driveContents)
                            .setResultCallback(fileCallback)
                }
            }.start()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            super.onActivityResult(requestCode, resultCode, data)
            when (requestCode) {
                REQUEST_CODE_CREATOR -> {
                    ToastUtils.with(activity).setMessage("Done").showShortToast()
                    if (resultCode == Activity.RESULT_OK) {
                        //startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        //                                REQUEST_CODE_SAVE_TO_DRIVE);
                        ToastUtils.with(activity).setMessage("Done").showShortToast()
                    }
                }
                REQUEST_CODE_SAVE_TO_DRIVE -> {
                }
            }//                    if (resultCode == Activity.RESULT_OK) {
            //                        // Store the image data as a bitmap for writing later.
            //                    }
        }

        override fun onConnectionFailed(result: ConnectionResult) {
            mCircleProgressDialogFragment!!.dismiss()
            // Called whenever the API client fails to connect.

            LogUtils.getInstance(TAG)
                    .setMessage("GoogleApiClient connection failed: " + result.toString())
                    .info()
            if (!result.hasResolution()) {
                // show the localized error dialog.
                GoogleApiAvailability.getInstance().getErrorDialog(activity, result.errorCode, 0).show()
                return
            }
            // The failure has a resolution. Resolve it.
            // Called typically when the app is not yet authorized, and an
            // authorization
            // dialog is displayed to the user.
            try {
                result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION)
            } catch (e: IntentSender.SendIntentException) {
                LogUtils.getInstance(TAG)
                        .setMessage("Exception while starting resolution activity")
                        .error(e)
            }

        }

        override fun onConnected(connectionHint: Bundle?) {
            mCircleProgressDialogFragment!!.dismiss()
            LogUtils.getInstance(TAG)
                    .setMessage("API client connected.")
                    .info()
            SnackBarUtils.shortSnackBar(view, "Google Drive connected!", SnackBarUtils.TYPE_INFO).show()
        }

        override fun onConnectionSuspended(cause: Int) {
            mCircleProgressDialogFragment!!.dismiss()
            LogUtils.getInstance(TAG)
                    .setMessage("GoogleApiClient connection suspended")
                    .info()
        }

        companion object {
            private val CONNECT_TO_DRIVE = "connect_to_drive"
            private val BACKUP_TO_DRIVE = "backup_to_drive"
            private val RESTORE_FROM_DRIVE = "restore_from_drive"
            private val BACKUP_TO_SD = "backup_to_sd"
            private val RESTORE_FROM_SD = "restore_from_sd"
            private val REQUEST_CODE_CREATOR = 2
            private val REQUEST_CODE_RESOLUTION = 3
            private val REQUEST_CODE_SAVE_TO_DRIVE = 1
        }

    }

    @SuppressLint("ValidFragment")
    class AboutPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_about)
            setHasOptionsMenu(true)
            val policy = findPreference(POLICY)
            val license = findPreference(LICENSE)
            val about = findPreference(ABOUT)
            policy.onPreferenceClickListener = this
            license.onPreferenceClickListener = this
            about.onPreferenceClickListener = this
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onPreferenceClick(preference: Preference): Boolean {
            val intent = Intent(activity, SettingsSecondActivity::class.java)
            when (preference.key) {
                POLICY -> {
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_POLICY)
                    startActivity(intent)
                }
                LICENSE -> {
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_LICENSE)
                    startActivity(intent)
                }
                ABOUT -> {
                    intent.putExtra(Constants.EXTRA_TYPE_FRAGMENT, Constants.FRAGMENT_ABOUT)
                    startActivity(intent)
                }
            }

            return false
        }

        companion object {
            private val POLICY = "settings_policy"
            private val LICENSE = "settings_license"
            private val ABOUT = "settings_about"
        }
    }

    companion object {
        private val TAG = "SettingsActivity"
        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            listPreference.entries[index]
                        else
                            null)

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }

}
