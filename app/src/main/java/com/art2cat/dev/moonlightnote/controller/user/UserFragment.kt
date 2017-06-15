package com.art2cat.dev.moonlightnote.controller.user


import android.Manifest
import android.support.v4.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.utils.PermissionUtils
import com.art2cat.dev.moonlightnote.utils.SPUtils
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils
import com.art2cat.dev.moonlightnote.utils.image_loader.BitmapUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.io.File
import java.io.IOException
import java.util.UUID

import de.hdodenhof.circleimageview.CircleImageView
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

import android.app.Activity.RESULT_OK
import com.art2cat.dev.moonlightnote.model.Constants.Companion.ALBUM_CHOOSE
import com.art2cat.dev.moonlightnote.model.Constants.Companion.BUS_FLAG_ALBUM
import com.art2cat.dev.moonlightnote.model.Constants.Companion.BUS_FLAG_CAMERA
import com.art2cat.dev.moonlightnote.model.Constants.Companion.BUS_FLAG_DELETE_ACCOUNT
import com.art2cat.dev.moonlightnote.model.Constants.Companion.BUS_FLAG_EMAIL
import com.art2cat.dev.moonlightnote.model.Constants.Companion.BUS_FLAG_USERNAME
import com.art2cat.dev.moonlightnote.model.Constants.Companion.CAMERA_PERMS
import com.art2cat.dev.moonlightnote.model.Constants.Companion.FB_STORAGE_REFERENCE
import com.art2cat.dev.moonlightnote.model.Constants.Companion.FILE_PROVIDER
import com.art2cat.dev.moonlightnote.model.Constants.Companion.STORAGE_PERMS
import com.art2cat.dev.moonlightnote.model.Constants.Companion.TAKE_PICTURE


/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment(), View.OnClickListener {
    private var mView: View? = null
    private var mCircleImageView: CircleImageView? = null
    private var mNickname: AppCompatTextView? = null
    private var mEmail: AppCompatTextView? = null
    private var mAdView: AdView? = null
    private var mCircleProgressDialogFragment: CircleProgressDialogFragment? = null
    private var user: FirebaseUser? = null
    private var mUser: User? = null
    private var mBitmapUtils: BitmapUtils? = null
    private var mFileUri: Uri? = null
    private var mStorageReference: StorageReference? = null
    private var mFileName: String? = null
    private val isChangePwd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取Bus单例，并注册
        EventBus.getDefault().register(this)
        //获取FirebaseUser对象
        user = FirebaseAuth.getInstance().currentUser
        //获取firebaseStorage实例
        mStorageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(FB_STORAGE_REFERENCE)


        mBitmapUtils = BitmapUtils(activity)

        mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater!!.inflate(R.layout.fragment_user, container, false)
        mCircleImageView = mView!!.findViewById(R.id.user_head_picture) as CircleImageView
        mNickname = mView!!.findViewById(R.id.user_nickname) as AppCompatTextView
        mEmail = mView!!.findViewById(R.id.user_email) as AppCompatTextView
        mAdView = mView!!.findViewById(R.id.banner_adView) as AdView

        activity.setTitle(R.string.title_activity_user)


        mUser = UserUtils.getUserFromCache(activity.applicationContext)
        LogUtils.getInstance(TAG).setMessage("displayUserInfo: " + mUser!!.getUid()).debug()

        val adRequest = AdRequest.Builder()
                //                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //                .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
                //                .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
                .build()
        mAdView!!.loadAd(adRequest)

        initView()
        return mView
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!SPUtils.getBoolean(activity, "User", "google", false)) {
            mCircleImageView!!.setOnClickListener(this)
            mNickname!!.setOnClickListener(this)
            setHasOptionsMenu(true)
        }

        mAdView!!.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                CircularRevealUtils.show(mAdView)
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        mAdView!!.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_change_password -> {
                val fragment = ChangePasswordFragment()
                FragmentUtils.replaceFragment(fragmentManager,
                        R.id.common_fragment_container,
                        fragment,
                        FragmentUtils.REPLACE_BACK_STACK)
            }
            R.id.action_close_account -> {
                val confirmationDialogFragment = ConfirmationDialogFragment.newInstance(getString(R.string.delete_account_title),
                        getString(R.string.delete_account_content),
                        Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT)
                confirmationDialogFragment.show(activity.fragmentManager, "delete account")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        val nickname = mUser!!.getNickname()
        LogUtils.getInstance(TAG).setMessage("initView: " + nickname!!).debug()
        if (nickname != null) {
            mNickname!!.setText(nickname)
            mUser!!.setNickname(nickname)
        } else {
            mNickname!!.setText(R.string.user_setNickname)
        }
        val email = mUser!!.getEmail()
        LogUtils.getInstance(TAG).setMessage("initView: " + email!!).debug()
        if (email != null) {
            mUser!!.setEmail(email)
            mEmail!!.setText(email)
        }

        val url = mUser!!.getPhotoUrl()
        LogUtils.getInstance(TAG).setMessage("initView: " + url!!)
        if (url != null) {

            mBitmapUtils!!.display(mCircleImageView, url)
        }
    }

    private fun updateUI(mDownloadUrl: Uri?) {
        if (mDownloadUrl != null) {
            val bitmapUtils = BitmapUtils(activity)
            bitmapUtils.display(mCircleImageView, mDownloadUrl.toString())
            mUser!!.setPhotoUrl(mDownloadUrl.toString())
        } else {
            mFileName = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.user_head_picture -> {
                val pickPicFragment = PickPicDialogFragment()
                pickPicFragment.show(fragmentManager, "PICK_PIC")
            }
            R.id.user_nickname -> showDialog(1)
        }
    }

    private fun showDialog(type: Int) {
        when (type) {
            1 -> {
                val inputDialogFragment1 = InputDialogFragment
                        .newInstance(getString(R.string.dialog_set_nickname), 1)
                inputDialogFragment1.show(activity.fragmentManager, "setNickname")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun busAction(busEvent: BusEvent?) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            when (busEvent!!.getFlag()) {
                BUS_FLAG_CAMERA -> {
                    Log.d(TAG, "busEvent: " + busEvent!!.getFlag())
                    onCameraClick()
                }
                BUS_FLAG_ALBUM -> {
                    Log.d(TAG, "busEvent: " + busEvent!!.getFlag())
                    onAlbumClick()
                }
                BUS_FLAG_USERNAME -> if (busEvent!!.getMessage() != null) {
                    mNickname!!.setText(busEvent!!.getMessage())
                    mUser!!.setNickname(busEvent!!.getMessage())
                    UserUtils.saveUserToCache(activity.applicationContext, mUser)
                    updateProfile(busEvent!!.getMessage(), null)
                }
                BUS_FLAG_EMAIL -> if (busEvent!!.getMessage().contains("@")) {
                    AuthUtils.sendRPEmail(activity, mView, busEvent!!.getMessage())
                }
                BUS_FLAG_DELETE_ACCOUNT -> if (busEvent!!.getMessage() != null) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val credential = EmailAuthProvider
                            .getCredential(user!!.email!!, busEvent!!.getMessage())
                    FDatabaseUtils.emptyNote(user.uid)
                    FDatabaseUtils.emptyTrash(user.uid)

                    user.reauthenticate(credential).addOnCompleteListener {
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                LogUtils.getInstance(TAG).setMessage("User account deleted.").debug()
                                startActivity(Intent(activity, LoginActivity::class.java))
                            }
                        }.addOnFailureListener { e -> if (BuildConfig.DEBUG) Log.d(TAG, e.toString()) }
                    }
                }
            }
        }
    }

    private fun updateProfile(nickname: String?, uri: Uri?) {
        var profileUpdates: UserProfileChangeRequest? = null
        if (nickname != null) {
            profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()
        }

        if (uri != null) {
            profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build()
            LogUtils.getInstance(TAG).setMessage("updateProfile: " + profileUpdates!!.photoUri!!.toString()).debug()
        }

        if (profileUpdates != null) {
            user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            LogUtils.getInstance(TAG).setMessage("User profile updated.").debug()
                            if (mUser != null) {
                                BusEventUtils.post(Constants.BUS_FLAG_UPDATE_USER, null)
                                UserUtils.updateUser(user!!.uid, mUser)
                            }
                        }
                    }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            TAKE_PICTURE -> if (resultCode == RESULT_OK && mFileUri != null) {
                uploadFromUri(data!!.data, user!!.uid)
            }
            ALBUM_CHOOSE -> if (resultCode == RESULT_OK && mFileUri != null) {
                uploadFromUri(data!!.data, user!!.uid)
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @AfterPermissionGranted(CAMERA_PERMS)
    private fun onCameraClick() {
        // Check that we have permission to read images from external storage.
        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val perm1 = Manifest.permission.CAMERA
        if (!EasyPermissions.hasPermissions(activity, perm) && !EasyPermissions.hasPermissions(activity, perm1)) {
            PermissionUtils.requestStorage(activity, perm)
            PermissionUtils.requestCamera(activity, perm1)
            return
        }
        if (!EasyPermissions.hasPermissions(activity, perm)) {
            PermissionUtils.requestStorage(activity, perm)
            return
        }
        if (!EasyPermissions.hasPermissions(activity, perm1)) {
            PermissionUtils.requestCamera(activity, perm1)
            return
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.image")
        val file = File(dir, UUID.randomUUID().toString() + ".jpg")

        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val created = file.createNewFile()
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile:" + file.absolutePath + ":" + created)
                    .debug()
        } catch (e: IOException) {
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile" + file.absolutePath + ":FAILED")
                    .error(e)
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, file)
        LogUtils.getInstance(TAG).setMessage("file: " + mFileName!!).info()
        // Create and launch the intent
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri)

        if (takePicIntent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE)
        } else {
            SnackBarUtils.longSnackBar(mView, "No Camera!", SnackBarUtils.TYPE_WARNING).show()
        }
    }

    @AfterPermissionGranted(STORAGE_PERMS)
    private fun onAlbumClick() {
        // Check that we have permission to read images from external storage.
        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (!EasyPermissions.hasPermissions(activity, perm)) {
            PermissionUtils.requestStorage(activity, perm)
            return
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.image")
        val file = File(dir, UUID.randomUUID().toString() + ".jpg")
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val created = file.createNewFile()
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile:" + file.absolutePath + ":" + created)
                    .debug()
        } catch (e: IOException) {
            LogUtils.getInstance(TAG)
                    .setMessage("file.createNewFile" + file.absolutePath + ":FAILED")
                    .error(e)
        }

        mFileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, file)
        LogUtils.getInstance(TAG).setMessage("file: " + mFileName!!).info()
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.type = "image/*"
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri)

        if (albumIntent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE)
        } else {
            SnackBarUtils.longSnackBar(mView, "No Album!", SnackBarUtils.TYPE_WARNING).show()
        }
    }

    private fun uploadFromUri(fileUri: Uri, userId: String) {

        mCircleProgressDialogFragment!!.show(activity.fragmentManager, "progress")

        val photoRef = mStorageReference!!.child(userId).child("avatar")
                .child(fileUri.lastPathSegment)

        // Upload file to Firebase Storage
        val uploadTask = photoRef.putFile(fileUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            mUser!!.setPhotoUrl(taskSnapshot.downloadUrl!!.toString())
            updateUI(taskSnapshot.downloadUrl)
            UserUtils.saveUserToCache(activity.applicationContext, mUser)
            updateProfile(null, taskSnapshot.downloadUrl)
            mCircleProgressDialogFragment!!.dismiss()
        }.addOnFailureListener { e ->
            mCircleProgressDialogFragment!!.dismiss()
            LogUtils.getInstance(TAG).setMessage("onFailure: ").error(e)
            mFileName = null
            if (user!!.photoUrl != null) {
                updateUI(user!!.photoUrl)
            }
        }
    }

    companion object {
        private val TAG = "UserFragment"
    }

}// Required empty public constructor
