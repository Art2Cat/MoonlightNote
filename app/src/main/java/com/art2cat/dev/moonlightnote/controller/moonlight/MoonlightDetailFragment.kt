package com.art2cat.dev.moonlightnote.controller.moonlight

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.design.widget.*
import android.support.v4.content.FileProvider
import android.support.v7.widget.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.BaseFragment
import com.art2cat.dev.moonlightnote.controller.BaseFragmentActivity
import com.art2cat.dev.moonlightnote.controller.BaseFragmentActivity.FragmentOnTouchListener
import com.art2cat.dev.moonlightnote.utils.FragmentBackHandler
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.CircleProgressDialogFragment
import com.art2cat.dev.moonlightnote.model.BusEvent
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.model.Constants.Companion.ALBUM_CHOOSE
import com.art2cat.dev.moonlightnote.model.Constants.Companion.TAKE_PICTURE
import com.art2cat.dev.moonlightnote.model.Moonlight
import com.art2cat.dev.moonlightnote.utils.*
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.art2cat.dev.moonlightnote.utils.firebase.StorageUtils
import com.art2cat.dev.moonlightnote.utils.material_animation.CircularRevealUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.*
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by Rorschach
 * on 24/05/2017 7:59 PM.
 */


/**
 * A simple [Fragment] subclass.
 */
abstract class MoonlightDetailFragment : BaseFragment(),
        View.OnClickListener, View.OnFocusChangeListener,
        PopupMenu.OnMenuItemClickListener, FragmentBackHandler {
    private var mView: View? = null
    private var mToolbar: Toolbar? = null
    private var mViewParent: ContentFrameLayout? = null
    private var mBottomBarContainer: LinearLayoutCompat? = null
    private var mAudioContainer: LinearLayoutCompat? = null
    private var mContentTextInputLayout: TextInputLayout? = null
    private var mTitle: TextInputEditText? = null
    private var mContent: TextInputEditText? = null
    private var mShowDuration: AppCompatTextView? = null
    private var mBottomBarLeft: AppCompatButton? = null
    private var mBottomBarRight: AppCompatButton? = null
    private var mDeleteAudio: AppCompatButton? = null
    private var mPlayingAudio: AppCompatButton? = null
    private var mAudioCardView: CardView? = null
    private var mImage: AppCompatImageView? = null
    private var mCircleProgressDialogFragment: CircleProgressDialogFragment? = null
    private var mCoordinatorLayout: CoordinatorLayout? = null
    private var mRightBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mLeftBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var moonlight: Moonlight? = null
    private var mFragmentOnTouchListener: FragmentOnTouchListener? = null
    private var mCreateFlag = true
    private var mEditFlag = false
    private var mEditable = true
    private var mStartPlaying = true
    private var isLeftOrRight: Boolean = false
    private var mUserId: String? = null
    private var mKeyId: String? = null
    private var mPaddingBottom: Int = 0
    private val mBottomBarHeight: Int = 0
    private var mFileUri: Uri? = null
    private var mStorageReference: StorageReference? = null
    private var mImageFileName: String? = null
    private var mAudioFileName: String? = null
    private var mDownloadIUrl: Uri? = null
    private var mDownloadAUrl: Uri? = null
    private var mFile: File? = null
    private val mHandler = Handler()
    private var mAudioPlayer: AudioPlayer? = null
    private var mColorMaps: MutableMap<Int, Int>? = null
    private var audioPlayerPB: ProgressBar? = null
    private var displayTime: TextView? = null

    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        LogUtils.getInstance(TAG).setMessage("onCreate").debug()
        //设置显示OptionsMenu
        setHasOptionsMenu(true)
        //获取Bus单例，并注册
        //BusProvider.getInstance().register(this);
        EventBus.getDefault().register(this)
        //获取用户id
        mUserId = FirebaseAuth.getInstance().currentUser!!.uid
        //获取firebaseStorage实例
        val firebaseStorage = FirebaseStorage.getInstance()
        mStorageReference = firebaseStorage.getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE)

        mInputMethodManager = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        mPaddingBottom = getResources().getDimensionPixelOffset(R.dimen.padding_bottom)

        if (getArguments() != null) {
            moonlight = getArguments().getParcelable("moonlight")
            if (moonlight != null) {
                mKeyId = moonlight!!.id
                if (BuildConfig.DEBUG) Log.d(TAG, "keyId: " + mKeyId!!)
            }
            val trashTag = getArguments().getInt("flag")
            if (trashTag == 0) {
                mEditFlag = true
                mCreateFlag = false
            } else {
                mEditFlag = true
                mCreateFlag = false
                mEditable = false
            }
        } else {
            moonlight = Moonlight()
        }

        (mActivity as DrawerLocker).setDrawerEnabled(false)

        initColor()

    }

    @SuppressLint("UseSparseArrays")
    private fun initColor() {
        mColorMaps = HashMap<Int, Int>()
        mColorMaps!!.put(Constants.AMBER, Constants.AMBER_DARK)
        mColorMaps!!.put(Constants.BLUE, Constants.BLUE_DARK)
        mColorMaps!!.put(Constants.BLUE_GRAY, Constants.BLUE_GRAY_DARK)
        mColorMaps!!.put(Constants.BROWN, Constants.BROWN_DARK)
        mColorMaps!!.put(Constants.CYAN, Constants.CYAN_DARK)
        mColorMaps!!.put(Constants.DEEP_ORANGE, Constants.DEEP_ORANGE_DARK)
        mColorMaps!!.put(Constants.DEEP_PURPLE, Constants.DEEP_PURPLE_DARK)
        mColorMaps!!.put(Constants.GREEN, Constants.GREEN_DARK)
        mColorMaps!!.put(Constants.GREY, Constants.GREY_DARK)
        mColorMaps!!.put(Constants.INDIGO, Constants.INDIGO_DARK)
        mColorMaps!!.put(Constants.LIGHT_BLUE, Constants.LIGHT_BLUE_DARK)
        mColorMaps!!.put(Constants.LIGHT_GREEN, Constants.LIGHT_GREEN_DARK)
        mColorMaps!!.put(Constants.LIME, Constants.LIME_DARK)
        mColorMaps!!.put(Constants.ORANGE, Constants.ORANGE_DARK)
        mColorMaps!!.put(Constants.PINK, Constants.PINK_DARK)
        mColorMaps!!.put(Constants.PURPLE, Constants.PURPLE_DARK)
        mColorMaps!!.put(Constants.RED, Constants.RED_DARK)
        mColorMaps!!.put(Constants.TEAL, Constants.TEAL_DARK)
        mColorMaps!!.put(Constants.YELLOW, Constants.YELLOW_DARK)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        LogUtils.getInstance(TAG).setMessage("onCreate").debug()
        //视图初始化
        mView = inflater.inflate(R.layout.fragment_moonlight_detail, container, false)

        mToolbar = (mActivity as MoonlightActivity).mToolbar2
        mToolbar!!.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar!!.setBackgroundColor(getResources().getColor(R.color.white, null))
        } else {
            mToolbar!!.setBackgroundColor(getResources().getColor(R.color.white))
        }
        val params = (mActivity as MoonlightActivity).mToolbar!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        (mActivity as MoonlightActivity).mToolbar!!.layoutParams = params
        mToolbar!!.layoutParams = params
        (mActivity as MoonlightActivity).setSupportActionBar(mToolbar)
        (mActivity as MoonlightActivity).mToolbar!!.visibility = View.GONE
        mToolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_grey_700_24dp)
        mToolbar!!.setNavigationOnClickListener { mActivity.onBackPressed() }

        mViewParent = mView!!.findViewById(R.id.view_parent)
        mTitle = mView!!.findViewById(R.id.title_TIET)
        mContent = mView!!.findViewById(R.id.content_TIET)
        mContentTextInputLayout = mView!!.findViewById(R.id.content_TIL)
        mImage = mView!!.findViewById(R.id.moonlight_image)
        mAudioCardView = mView!!.findViewById(R.id.audio_container)
        mAudioContainer = mView!!.findViewById(R.id.audio_container_inner)

        mDeleteAudio = mView!!.findViewById(R.id.delete_audio)
        mPlayingAudio = mView!!.findViewById(R.id.playing_audio_button)
        mShowDuration = mView!!.findViewById(R.id.moonlight_audio_duration)
        audioPlayerPB = mView!!.findViewById(R.id.moonlight_audio_progressBar)!!
        displayTime = mView!!.findViewById(R.id.bottom_bar_display_time)
        mCoordinatorLayout = mView!!.findViewById(R.id.bottom_sheet_container)
        mBottomBarContainer = mView!!.findViewById(R.id.bottom_bar_container)
        mBottomBarLeft = mView!!.findViewById(R.id.bottom_bar_left)
        mBottomBarRight = mView!!.findViewById(R.id.bottom_bar_right)
        mTitle!!.onFocusChangeListener = this
        mContent!!.onFocusChangeListener = this
        mAudioPlayer = AudioPlayer.getInstance(audioPlayerPB!!, mShowDuration as AppCompatTextView)

        mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance(getString(R.string.prograssBar_uploading))

        if (mEditable) {
            //获取系统当前时间
            val date = System.currentTimeMillis()
            moonlight!!.date = date
            val time = Utils.timeFormat(mActivity.getApplicationContext(), Date(date))
            if (time != null) {
                val timeFormat = "Edited: " + time
                displayTime!!.text = timeFormat
            }
            onCheckSoftKeyboardState(mView!!)
            mImage!!.setOnClickListener(this)
            mDeleteAudio!!.setOnClickListener(this)
            mPlayingAudio!!.setOnClickListener(this)
            //            if (!Utils.isXLargeTablet(mActivity)) {
            showBottomSheet()
            //            }

            mBottomBarLeft!!.setOnClickListener(this)
            mBottomBarRight!!.setOnClickListener(this)
        }

        mTitle!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val title = s.toString()
                moonlight!!.title = title
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        mContent!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val content = s.toString()
                moonlight!!.content = content
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        return mView as View
    }

    private fun initView(editable: Boolean) {
        if (editable) {
            mTitle!!.setText(moonlight!!.title)
        } else {
            mTitle!!.isEnabled = false
            if (moonlight!!.title != null) {
                mTitle!!.setText(moonlight!!.title)
            }
        }
        if (moonlight!!.content != null) {
            mContent!!.setText(moonlight!!.content)
            if (!editable) {
                mContent!!.isEnabled = false
            }
        }
        if (moonlight!!.imageUrl != null) {
            val url = moonlight!!.imageUrl
            Picasso.with(mActivity)
                    .load(Uri.parse(url))
//                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .into(mImage)
            mImage!!.post { CircularRevealUtils.get().show(mImage as AppCompatImageView) }

            mContentTextInputLayout!!.setPadding(0, 0, 0, mPaddingBottom)
        }
        showAudio(moonlight!!.audioName)
        mAudioCardView!!.visibility = View.VISIBLE
        mContentTextInputLayout!!.setPadding(0, 0, 0, 0)
        if (!editable) {
            mDeleteAudio!!.isClickable = false
            mBottomBarLeft!!.isClickable = false
            mBottomBarRight!!.isClickable = false
        }
        if (moonlight!!.color !== 0) {
            changeUIColor(moonlight!!.color)
        }
    }

    private fun changeUIColor(@ColorRes color: Int, theme: Resources.Theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar!!.setBackgroundColor(getResources().getColor(color, theme))
            mBottomBarContainer!!.setBackgroundColor(getResources().getColor(color, theme))
        } else {
            mToolbar!!.setBackgroundColor(getResources().getColor(color))
            mBottomBarContainer!!.setBackgroundColor(getResources().getColor(color))
        }
    }

    private fun changeUIColor(@ColorInt color: Int) {
        mViewParent!!.setBackgroundColor(color)
        mToolbar!!.setBackgroundColor(color)
        mAudioContainer!!.setBackgroundColor(moonlight!!.color)
        mBottomBarContainer!!.setBackgroundColor(color)
        changeStatusBarColor(color)
    }

    private fun changeStatusBarColor(color: Int) {
        for (integer in mColorMaps!!.keys) {
            if (integer === color) {
                mActivity.getWindow().setStatusBarColor(mColorMaps!![color] as Int)
                break
            }
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        super.onStart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        changeUIColor(R.color.white, mActivity.getTheme())
        mToolbar!!.title = null
        (mActivity as MoonlightActivity).mToolbar!!.title = null
        (mActivity as MoonlightActivity).hideFAB()
        initView(mEditable)
        setOverflowButtonColor(mActivity, Constants.GREY_DARK)

        //        if (Utils.isXLargeTablet(mActivity)) {
        //            LinearLayout.LayoutParams lp =
        //                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //                            mToolbar.getHeight());
        //            mBottomBarContainer.setLayoutParams(lp);
        //        }

        if (!mEditable) {
            //禁用软键盘
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            val snackbar = SnackBarUtils.longSnackBar(mView as View, getString(R.string.trash_restore),
                    SnackBarUtils.TYPE_WARNING).setAction(R.string.trash_restore_action
            ) {
                BusEventUtils.post(Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT, null)
                FDatabaseUtils.restoreToNote(mUserId as String, moonlight as Moonlight)
                getFragmentManager().popBackStack()
            }

            mFragmentOnTouchListener =
                    object : BaseFragmentActivity.FragmentOnTouchListener {
                        override fun onTouch(ev: MotionEvent): Boolean {
                            if (!snackbar.isShown && ev.action == MotionEvent.ACTION_DOWN) {
                                snackbar.show()
                            }

                            return false
                        }
                    }
            (mActivity as MoonlightActivity).registerFragmentOnTouchListener(mFragmentOnTouchListener as FragmentOnTouchListener)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        Log.d(TAG, "onResume: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (mInputMethodManager != null) {
            mInputMethodManager!!.hideSoftInputFromWindow(
                    mActivity.getWindow().getDecorView().getWindowToken(), 0)

        }

        revertUI()

        mAudioPlayer!!.releasePlayer()
        //移除FragmentOnTouchListener
        if (mFragmentOnTouchListener != null) {
            (mActivity as MoonlightActivity).unregisterFragmentOnTouchListener(mFragmentOnTouchListener as FragmentOnTouchListener)
        }

        val refWatcher = MoonlightApplication.getRefWatcher(mActivity)
        refWatcher.watch(this)
        release()
        super.onDestroy()
    }

    private fun release() {
        //        mView = null;
        mToolbar = null
        mViewParent = null
        mBottomBarContainer = null
        mAudioContainer = null
        mContentTextInputLayout = null
        mTitle = null
        mContent = null
        mShowDuration = null
        mBottomBarLeft = null
        mBottomBarRight = null
        mDeleteAudio = null
        mPlayingAudio = null
        mAudioCardView = null
        mImage = null
        mCoordinatorLayout = null
        moonlight = null
    }

    override fun onBackPressed(): Boolean {
        commitMoonlight()
        val fragmentManager = mActivity.getFragmentManager()
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack()
        }
        return false
    }

    private fun commitMoonlight() {
        //当moonlight图片，标题，内容不为空空时，添加moonlight到服务器
        if (mCreateFlag && mEditable) {
            if (!isEmpty(moonlight as Moonlight)) {
                FDatabaseUtils.addMoonlight(mUserId as String, moonlight as Moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
            }
        }
        //当editFlag为true且moonlight不为空时更新moonlight信息到服务器
        if (mEditable && mEditFlag && moonlight != null && !moonlight!!.isTrash) {
            FDatabaseUtils.updateMoonlight(mUserId as String, mKeyId, moonlight as Moonlight,
                    Constants.EXTRA_TYPE_MOONLIGHT)
        }
    }

    /**
     * 恢复原来的UI界面
     */
    private fun revertUI() {

        mToolbar!!.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mToolbar!!.setBackgroundColor(getResources().getColor(R.color.light_green, null))
        } else {
            mToolbar!!.setBackgroundColor(getResources().getColor(R.color.light_green))
        }
        val params = (mActivity as MoonlightActivity).mToolbar!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        (mActivity as MoonlightActivity).mToolbar!!.layoutParams = params
        (mActivity as MoonlightActivity).mToolbar!!.visibility = View.VISIBLE
        (mActivity as MoonlightActivity).mToolbar!!.setTitle(getString(R.string.app_name))

        (mActivity as DrawerLocker).setDrawerEnabled(true)

        if (mEditable) {
            mActivity.getWindow().setStatusBarColor(Constants.CYAN_DARK)
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT)

            if ((mActivity as MoonlightActivity).mFAB != null) {
                (mActivity as MoonlightActivity).mFAB!!.show()
            }

        }

    }

    @SuppressLint("LogConditional")
    override fun onFocusChange(view: View, b: Boolean) {
        when (view.id) {
            R.id.title_TIET -> Log.d(TAG, "onFocusChange: title " + b)
            R.id.content_TIET -> Log.d(TAG, "onFocusChange: content " + b)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //如mEditFlag为true，加载edit_moonlight_menu，反之则加载create_moonlight_menu
        if (mCreateFlag || mEditFlag) {
            inflater.inflate(R.menu.menu_moonlight_detail, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_color_picker -> if (mEditable) {
                val dialog = MyColorPickerDialog(mActivity)
                dialog.setTitle("Color Picker")
                dialog.setColorListener { v, color ->
                    moonlight!!.color = color
                    changeUIColor(color)
                    mEditable = true
                    mEditable = true
                }
                //customize the dialog however you want
                dialog.show()
            }
            R.id.action_remove_image -> {
                if (BuildConfig.DEBUG) showShortToast("delete image")
                StorageUtils.removePhoto(mView, mUserId!!, moonlight!!.imageName)
                CircularRevealUtils.get().hide(mImage!!)
                moonlight!!.imageName = ""
                moonlight!!.imageUrl = ""
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isEmpty(moonlight: Moonlight): Boolean {
        return !(moonlight.imageUrl != null || moonlight.audioUrl != null || moonlight.content != null
                || moonlight.title != null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleMessage(busEvent: BusEvent?) {

        when (busEvent!!.flag) {
            Constants.BUS_FLAG_AUDIO_URL ->
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "handleMessage: " + busEvent!!.message)
                }
//                var file = File(File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.audio"), busEvent!!.message)
//            mAudioUri = FileProvider.getUriForFile(MoonlightApplication.context, Constants.FILE_PROVIDER, file)
//                    uploadFromUri (mAudioUri, mUserId, 3)

        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bottom_bar_left -> {
                //                if (Utils.isXLargeTablet(mActivity)) {
                //                    MenuUtils.showPopupMenu(mActivity, mView, R.menu.menu_detail_left, this);
                //                } else {
                isLeftOrRight = true
                hideSoftKeyboard()
            }
            R.id.bottom_bar_right -> {
                //                if (Utils.isXLargeTablet(mActivity)) {
                //                    MenuUtils.showPopupMenu(mActivity, mView, R.menu.menu_detail_right, this);
                //                } else {
                isLeftOrRight = false
                hideSoftKeyboard()
            }
            R.id.moonlight_image -> {
                //网页浏览图片。。。
                val scaleFragment = ScaleFragment.newInstance(moonlight!!.imageUrl)
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, scaleFragment)
                        .addSharedElement(mImage, mImage!!.transitionName)
                        .addToBackStack("scale")
                        .commit()
            }
            R.id.playing_audio_button -> if (moonlight!!.audioName != null && mStartPlaying) {
                mStartPlaying = false
                if (!mAudioPlayer!!.isPrepared) {
                    mAudioPlayer!!.prepare(moonlight!!.audioName)
                }
                mAudioPlayer!!.startPlaying()
                mPlayingAudio!!.setBackgroundResource(R.drawable.ic_pause_circle_outline_lime_a700_24dp)
                mAudioPlayer!!.mPlayer!!.setOnCompletionListener { mediaPlayer ->
                    mediaPlayer.reset()
                    mAudioPlayer!!.mProgressBar.progress = 0
                    mPlayingAudio!!.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp)
                    mAudioPlayer!!.isPrepared = false
                    mStartPlaying = true
                }
            } else {
                mAudioPlayer!!.stopPlaying()
                mPlayingAudio!!.setBackgroundResource(R.drawable.ic_play_circle_outline_cyan_400_48dp)
                mStartPlaying = true
                mAudioPlayer!!.mPlayer!!.reset()
                mAudioPlayer!!.isPrepared = false
            }
            R.id.delete_audio -> {
                //删除录音
                StorageUtils.removeAudio(mView, mUserId as String, moonlight!!.audioName)
                mAudioCardView!!.visibility = View.GONE
                moonlight!!.audioName = ""
                moonlight!!.audioUrl = ""
            }
            R.id.bottom_sheet_item_take_photo -> onCameraClick()
            R.id.bottom_sheet_item_choose_image -> onAlbumClick()
            R.id.bottom_sheet_item_recording -> onAudioClick()
            R.id.bottom_sheet_item_move_to_trash -> {
                if (!isEmpty(moonlight as Moonlight)) {
                    FDatabaseUtils.moveToTrash(mUserId as String, moonlight as Moonlight)
                    //                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_DELETE);
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null)
                }
                mActivity.onBackPressed()
                mEditable = false
            }
            R.id.bottom_sheet_item_permanent_delete -> {
                if (!isEmpty(moonlight as Moonlight)) {
                    StorageUtils.removePhoto(mView, mUserId as String, moonlight!!.imageName)
                    StorageUtils.removeAudio(mView, mUserId as String, moonlight!!.audioName)
                    if (mKeyId != null) {
                        FDatabaseUtils.removeMoonlight(mUserId as String, mKeyId as String, Constants.EXTRA_TYPE_MOONLIGHT)
                    }
                    //                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_PERMENAT_DELETE);
                    moonlight = null
                } else {
                    BusEventUtils.post(Constants.BUS_FLAG_NULL, null)
                }
                mActivity.onBackPressed()
            }
            R.id.bottom_sheet_item_make_a_copy -> if (!isEmpty(moonlight!!)) {
                FDatabaseUtils.addMoonlight(mUserId as String, moonlight as Moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
                //                    BusEventUtils.post(moonlight, Constants.BUS_FLAG_MAKE_A_COPY);
                showShortSnackBar(mViewParent!!,
                        "Note Copy complete.", SnackBarUtils.TYPE_INFO)
                changeBottomSheetState()
            } else {
                showShortSnackBar(mViewParent!!,
                        getString(R.string.note_binned), SnackBarUtils.TYPE_INFO)
                changeBottomSheetState()
            }
            R.id.bottom_sheet_item_send -> {
                //启动Intent分享
                var `in` = Intent(Intent.ACTION_SEND)
                `in`.type = "text/plain"
                if (moonlight!!.title != null) {
                    `in`.putExtra(Intent.EXTRA_TITLE, moonlight!!.title)
                }

                if (moonlight!!.content != null) {
                    `in`.putExtra(Intent.EXTRA_TEXT, moonlight!!.content)
                }

                if (moonlight!!.imageUrl != null) {
                    `in`.putExtra(Intent.EXTRA_TEXT, moonlight!!.imageUrl)
                }
                //设置分享选择器
                `in` = Intent.createChooser(`in`, "Send to")
                startActivity(`in`)
            }
        }//                }
        //                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            TAKE_PICTURE -> {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "take Picture" + mFileUri!!.toString())
                    uploadFromUri(mFileUri!!, mUserId!!, 0)
                }
                mEditable = true
            }
            ALBUM_CHOOSE -> {
                Log.d(TAG, "album choose")
                if (resultCode == RESULT_OK && data.data != null) {
                    val fileUri = data.data
                    uploadFromUri(fileUri, mUserId!!, 0)
                }
                mEditable = true
            }
            RECORD_AUDIO -> if (resultCode == RESULT_OK) {
                val results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS)
                val spokenText = results[0]
                // Do something with spokenText
                mContent!!.setText(spokenText)
                Log.d(TAG, "onActivityResult: " + spokenText)
                // the recording url is in getData:
                if (data.data != null) {
                    val audioUri = data.data
                    Log.d(TAG, "onActivityResult: " + audioUri.toString())
                    if (copyAudioFile(audioUri) != null) {
                        uploadFromUri(copyAudioFile(audioUri)!!, mUserId!!, 3)
                    }
                }
                mEditable = true
            }
            else -> {
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun copyAudioFile(uri: Uri): Uri? {
        val contentResolver = mActivity.getContentResolver()
        val pwd = mActivity.getCacheDir().getAbsolutePath()
        val dir = File(pwd + "/audio")
        if (!dir.exists()) {
            val isDirCreate = dir.mkdirs()
            Log.d(TAG, "dir.mkdirs():" + isDirCreate)
        }
        val file = File(dir, UUID.randomUUID().toString() + ".amr")
        file.copyTo()
        val fos: FileOutputStream
        val inputStream: InputStream?
        try {
            inputStream = contentResolver.openInputStream(uri)
            fos = FileOutputStream(file)
            try {

                val buffer = ByteArray(4 * 1024)
                var length: Int
                while ((length = if (inputStream != null) inputStream!!.read(buffer) else 0) != -1) {
                    fos.write(buffer, 0, length)
                }
                fos.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {

                fos.close()
                assert(inputStream != null)

                inputStream!!.close()
            }
            return Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    @SuppressLint("LogConditional")
    @AfterPermissionGranted(105)
    private fun onCameraClick() {
        // Check that we have permission to read images from external storage.
        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val perm1 = Manifest.permission.CAMERA
        if (!EasyPermissions.hasPermissions(mActivity, perm) && !EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm)
            PermissionUtils.requestCamera(mActivity, perm1)
            return
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestStorage(mActivity, perm)
            return
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestCamera(mActivity, perm1)
            return
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.image")
        mFile = File(dir, UUID.randomUUID().toString() + ".jpg")
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val created = mFile!!.createNewFile()
            Log.d(TAG, "created:" + created)
        } catch (e: IOException) {
            Log.e(TAG, "file.createNewFile" + mFile!!.absolutePath + ":FAILED", e)
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html

        mFileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, mFile)
        Log.i(TAG, "file: " + mFileUri!!)

        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri)

        if (takePicIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(takePicIntent, TAKE_PICTURE)
            mEditable = false
            Log.d(TAG, "onCameraClick: ")
        } else {
            showLongSnackBar(mView!!, "No Camera!", SnackBarUtils.TYPE_WARNING)
        }
    }

    @SuppressLint("LogConditional")
    @AfterPermissionGranted(101)
    private fun onAlbumClick() {
        // Check that we have permission to read images from external storage.
        val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestStorage(mActivity, perm)
            return
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.image")
        mFile = File(dir, UUID.randomUUID().toString() + ".jpg")
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val created = mFile!!.createNewFile()
            Log.d(TAG, "file.createNewFile:" + mFile!!.absolutePath + ":" + created)
        } catch (e: IOException) {
            Log.e(TAG, "file.createNewFile" + mFile!!.absolutePath + ":FAILED", e)
        }

        val fileUri = FileProvider.getUriForFile(mActivity, Constants.FILE_PROVIDER, mFile)
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.type = "image/*"
        albumIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

        if (albumIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            startActivityForResult(albumIntent, ALBUM_CHOOSE)
            mEditable = false
        } else {
            showLongSnackBar(mView!!, "No Album!", SnackBarUtils.TYPE_WARNING)
        }
    }

    @AfterPermissionGranted(104)
    private fun onAudioClick() {
        // Check that we have permission to read images from external storage.
        val perm = Manifest.permission.RECORD_AUDIO
        val perm1 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (!EasyPermissions.hasPermissions(mActivity, perm) && !EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm1)
            PermissionUtils.requestRecAudio(mActivity, perm)
            return
        }
        if (!EasyPermissions.hasPermissions(mActivity, perm1)) {
            PermissionUtils.requestStorage(mActivity, perm1)
            return
        }

        if (!EasyPermissions.hasPermissions(mActivity, perm)) {
            PermissionUtils.requestRecAudio(mActivity, perm)
            return
        }
        // Choose file storage location, must be listed in res/xml/file_paths.xml
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/MoonlightNote/.audio")

        if (!dir.exists()) {
            val isDirCreate = dir.mkdirs()
            Log.d(TAG, "onAudioClick: " + isDirCreate)
        }
        displaySpeechRecognizer()
    }

    fun uploadFromUri(fileUri: Uri, userId: String, type: Int) {
        if (mCircleProgressDialogFragment != null) {
            mCircleProgressDialogFragment!!.show(mActivity.getFragmentManager(), "progress")
        } else {
            mCircleProgressDialogFragment = CircleProgressDialogFragment.newInstance()
            mCircleProgressDialogFragment!!.show(mActivity.getFragmentManager(), "progress")
        }

        val uploadTask: StorageTask<UploadTask.TaskSnapshot>
        if (type == 0) {

            // Get a reference to store file at photos/<FILENAME>.jpg
            val photoRef = mStorageReference!!.child(userId).child("photos")
                    .child(fileUri.lastPathSegment)
            Log.d(TAG, "uploadFromUri: " + fileUri.lastPathSegment)
            // Upload file to Firebase Storage
            uploadTask = photoRef.putFile(fileUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                mDownloadIUrl = taskSnapshot.downloadUrl
                mImageFileName = taskSnapshot.metadata!!.name

                Log.d(TAG, "onSuccess: downloadUrl:  " + mDownloadIUrl!!.toString())
                moonlight!!.imageName = mImageFileName!!
                moonlight!!.imageUrl = mDownloadIUrl!!.toString()
                mCircleProgressDialogFragment!!.dismiss()
                showImage(mDownloadIUrl)
            }.addOnFailureListener { e ->
                Log.e(TAG, "onFailure: " + e.toString())
                mImageFileName = null
                mDownloadIUrl = null
                mCircleProgressDialogFragment!!.dismiss()
                showImage(fileUri)
            }.addOnPausedListener {
                Log.d(TAG, "onPaused: ")
                mCircleProgressDialogFragment!!.dismiss()
                showShortSnackBar(mView as ContentFrameLayout, "upload paused", SnackBarUtils.TYPE_INFO)
            }
        } else if (type == 3) {
            val storageReference = mStorageReference!!.child(userId).child("audios")
                    .child(fileUri.lastPathSegment)
            uploadTask = storageReference.putFile(fileUri)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                mDownloadAUrl = taskSnapshot.downloadUrl
                mAudioFileName = taskSnapshot.metadata!!.name

                Log.d(TAG, "onSuccess: downloadUrl:  " + mAudioFileName!!)
                moonlight!!.audioName = (mAudioFileName as String)
                moonlight!!.audioUrl = (mDownloadAUrl!!.toString())
                showAudio(mAudioFileName!!)
                mCircleProgressDialogFragment!!.dismiss()
            }.addOnFailureListener { e ->
                Log.e(TAG, "onFailure: " + e.toString())
                mAudioFileName = null
                mDownloadAUrl = null
                mCircleProgressDialogFragment!!.dismiss()
                showAudio(fileUri.toString())
            }.addOnPausedListener {
                Log.d(TAG, "onPaused: ")
                mCircleProgressDialogFragment!!.dismiss()
                showShortSnackBar(mView as ContentFrameLayout, "upload paused", SnackBarUtils.TYPE_INFO)
            }
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private fun displaySpeechRecognizer() {
        Log.d(TAG, "displaySpeechRecognizer: ")
        loseFocus()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        // secret parameters that when added provide audio url in the result
        intent.putExtra(Constants.GET_AUDIO_FORMAT, "audio/AMR")
        intent.putExtra(Constants.GET_AUDIO, true)
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, RECORD_AUDIO)
        mEditable = false
    }

    fun showBottomSheet() {
        initBottomSheetItem()
        // The View with the BottomSheetBehavior

        val bottomSheetLeft = mCoordinatorLayout!!.findViewById(R.id.bottom_sheet_left)
        val bottomSheetRight = mCoordinatorLayout!!.findViewById(R.id.bottom_sheet_right)
        mLeftBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLeft)
        mRightBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetRight)
        mLeftBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        mRightBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
                if (bottomSheet === bottomSheetLeft) {
                    Log.d(TAG, "left onStateChanged: " + newState)
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        mLeftBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
                if (bottomSheet === bottomSheetRight) {
                    Log.d(TAG, "right onStateChanged: " + newState)
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        mRightBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }


                //                ViewCompat.setScaleX(bottomSheet,1);
                //                ViewCompat.setScaleY(bottomSheet,1);
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                //                ViewCompat.setScaleX(bottomSheet,slideOffset);
                //                ViewCompat.setScaleY(bottomSheet,slideOffset);
            }
        }

        mLeftBottomSheetBehavior!!.setBottomSheetCallback(bottomSheetCallback)
        mRightBottomSheetBehavior!!.setBottomSheetCallback(bottomSheetCallback)

    }

    private fun initBottomSheetItem() {
        val takePhoto = mView!!.findViewById(R.id.bottom_sheet_item_take_photo)
        val chooseImage = mView!!.findViewById(R.id.bottom_sheet_item_choose_image)!!
        val recording = mView!!.findViewById(R.id.bottom_sheet_item_recording)!!
        val moveToTrash = mView!!.findViewById(R.id.bottom_sheet_item_move_to_trash)
        val permanentDelete = mView!!.findViewById(R.id.bottom_sheet_item_permanent_delete)
        val makeACopy = mView!!.findViewById(R.id.bottom_sheet_item_make_a_copy)
        val send = mView!!.findViewById(R.id.bottom_sheet_item_send)
        takePhoto.setOnClickListener(this)
        chooseImage.setOnClickListener(this)
        recording.setOnClickListener(this)
        moveToTrash.setOnClickListener(this)
        permanentDelete.setOnClickListener(this)
        makeACopy.setOnClickListener(this)
        send.setOnClickListener(this)
    }

    private fun hideSoftKeyboard() {
        if (mInputMethodManager != null) {
            if (mEditable) {
                mInputMethodManager!!.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(), 0)
                mHandler.postDelayed(BottomSheet(), 100)
            }
        }
    }

    private fun showSoftKeyboard() {
        if (mInputMethodManager != null) {
            mInputMethodManager!!.showSoftInput(mActivity.getWindow().getDecorView(), 0)
        }
    }

    private fun changeBottomSheetState() {
        // isLeftOrRight值为真是左，假则是右
        if (isLeftOrRight) {
            // 首先检查RightBottomSheet是否启用，如果是则隐藏
            if (mRightBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || mRightBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                mRightBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            }
            //检查LeftBottomSheet是否为隐藏，如果是则直接展开，否则进入下一步判断
            if (mLeftBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HIDDEN) {
                mLeftBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                //检查LeftBottomSheet是否展开或者收缩，进行相应操作
                if (mLeftBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    mLeftBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    loseFocus()
                } else {
                    mLeftBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        } else {
            // 首先检查LeftBottomSheet是否启用，如果是则隐藏
            if (mLeftBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED || mLeftBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                mLeftBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            }
            //检查RightBottomSheet是否为隐藏，如果是则直接展开，否则进入下一步判断
            if (mRightBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_HIDDEN) {
                mRightBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                //检查RightBottomSheet是否展开或者收缩，进行相应操作
                if (mRightBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    mRightBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    loseFocus()
                } else {
                    mRightBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                }
            }
        }
    }

    private fun loseFocus() {
        Log.d(TAG, "loseFocus: ")
        mTitle!!.clearFocus()
        mContent!!.clearFocus()
    }

    /**
     * 用监听软键盘是否弹出

     * @param view 主视图布局
     */
    private fun onCheckSoftKeyboardState(view: View) {
        //先主视图布局设置监听，监听其布局发生变化事件
        view.viewTreeObserver
                .addOnGlobalLayoutListener {
                    //比较主视图布局与当前布局的大小
                    val heightDiff = mView!!.rootView.height - view.height
                    if (heightDiff > 100) {
                        //大小超过100时，一般为显示虚拟键盘事件
                        if (mEditable && !Utils.isXLargeTablet(mActivity)) {
                            if (mLeftBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                                mLeftBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                            }
                            if (mRightBottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                                mRightBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                            }
                        } else {
                            //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                            Log.d(TAG, "onGlobalLayout: ")
                        }
                    }
                }
    }

    /**
     * 更新显示图片信息

     * @param mFileUri 图片地址
     */
    private fun showImage(mFileUri: Uri?) {
        //当图片地址不为空时，首先从本地读取bitmap设置图片，bitmap为空，则从网络加载
        //图片地址为空则不加载图片
        if (mFileUri != null) {
            Picasso.with(mActivity)
                    .load(mFileUri)
//                    .memoryPolicy(NO_CACHE, NO_STORE)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .config(Bitmap.Config.RGB_565)
                    .into(mImage)
            mImage!!.post { CircularRevealUtils.get().show(mImage as AppCompatImageView) }

            mContentTextInputLayout!!.setPadding(0, 0, 0, mPaddingBottom)
        } else {
            mImageFileName = null
            //mImageCardView.setVisibility(View.GONE);
        }
    }

    private fun showAudio(audioFileName: String) {
        if (moonlight!!.audioDuration == 0L) {
            mAudioPlayer!!.prepare(audioFileName)
            moonlight!!.audioDuration = mAudioPlayer!!.mDuration.toLong()
            mAudioCardView!!.visibility = View.VISIBLE
            mAudioContainer!!.setBackgroundColor(moonlight!!.color)
            mContentTextInputLayout!!.setPadding(0, 0, 0, 0)
        } else {
            mShowDuration!!.text = Utils.convert(moonlight!!.audioDuration)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return false
    }

    private inner class BottomSheet : Runnable {

        override fun run() {
            changeBottomSheetState()
        }
    }

    companion object {
        private val TAG = "MoonlightDetailFragment"
    }
}// Required empty public constructor
