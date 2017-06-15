package com.art2cat.dev.moonlightnote.controller.moonlight

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.animation.OvershootInterpolator
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.BaseFragment
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.ConfirmationDialogFragment
import com.art2cat.dev.moonlightnote.model.BusEvent
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.model.Moonlight
import com.art2cat.dev.moonlightnote.utils.BusEventUtils
import com.art2cat.dev.moonlightnote.utils.FragmentUtils
import com.art2cat.dev.moonlightnote.utils.LogUtils
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.art2cat.dev.moonlightnote.utils.firebase.StorageUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * Created by Rorschach
 * on 24/05/2017 8:02 PM.
 */


abstract class MoonlightListFragment : BaseFragment() {
    private var mDatabase: DatabaseReference? = null
    private var mFirebaseRecyclerAdapter: FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>? = null
    private var mToolbar: Toolbar? = null
    private var mToolbar2: Toolbar? = null
    private var mParams: AppBarLayout.LayoutParams? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapterDataObserver: RecyclerView.AdapterDataObserver? = null
    private var mMoonlight: Moonlight? = null
    private var mMenu: Menu? = null
    private var mMenuInflater: MenuInflater? = null
    private var isLogin = true
    private var isInflate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取Bus单例，并注册
        EventBus.getDefault().register(this)
        LogUtils.getInstance(TAG).setMessage("onCreate").debug()
        //在配置变化的时候将这个fragment保存下来
        setRetainInstance(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        LogUtils.getInstance(TAG).setMessage("onCreateView").debug()
        val rootView = inflater.inflate(R.layout.fragment_moonlight, container, false)

        val moonlightActivity = mActivity as MoonlightActivity
        mToolbar = moonlightActivity.mToolbar
        mToolbar2 = moonlightActivity.mToolbar2
        mParams = mToolbar!!.layoutParams as AppBarLayout.LayoutParams

        if (isTrash) {
            mToolbar!!.setTitle(R.string.fragment_trash)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar!!.setBackgroundColor(getResources().getColor(R.color.grey,
                        mActivity.getTheme()))
                //                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.grey_dark,
                //                        mActivity.getTheme()));
            } else {
                mToolbar!!.setBackgroundColor(getResources().getColor(R.color.grey))
                //                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.grey_dark));
            }
        } else {
            mToolbar!!.setTitle(R.string.app_name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mToolbar!!.setBackgroundColor(getResources().getColor(R.color.colorPrimary, mActivity.getTheme()))
                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark,
                        mActivity.getTheme()))
            } else {
                mToolbar!!.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                mActivity.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark))
            }
            mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT)
        }
        setHasOptionsMenu(true)

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().reference

        // [END create_database_reference]
        mRecyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        LogUtils.getInstance(TAG).setMessage("onResume").debug()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isTrash) {
            (mActivity as MoonlightActivity).hideFAB()
            MoonlightActivity.isHome = false
        }

        LogUtils.getInstance(TAG).setMessage("onActivityCreated").debug()
        val mLinearLayoutManager = LinearLayoutManager(mActivity)
        mLinearLayoutManager.reverseLayout = true
        mLinearLayoutManager.stackFromEnd = true
        mRecyclerView!!.layoutManager = mLinearLayoutManager

        val animator = SlideInRightAnimator()
        animator.setInterpolator(OvershootInterpolator())
        mRecyclerView!!.itemAnimator = animator
        mRecyclerView!!.itemAnimator.addDuration = 500
        mRecyclerView!!.itemAnimator.removeDuration = 500
        mRecyclerView!!.itemAnimator.moveDuration = 500
        mRecyclerView!!.itemAnimator.changeDuration = 500
        setAdapter()

        mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(mActivity).resumeTag(tag)

                } else {
                    Picasso.with(mActivity).pauseTag(tag)
                }
            }

        })

        mRecyclerView!!.setOnDragListener { view, dragEvent -> false }

        setOverflowButtonColor(mActivity, 0xFFFFFFFF.toInt())

    }

    override fun onPause() {
        super.onPause()
        LogUtils.getInstance(TAG).setMessage("onPause").debug()
    }

    override fun onStop() {
        super.onStop()
        LogUtils.getInstance(TAG).setMessage("onStop").debug()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.getInstance(TAG).setMessage("onDestroyView").debug()
        isInflate = false
        mFirebaseRecyclerAdapter!!.unregisterAdapterDataObserver(mAdapterDataObserver)
    }

    private fun setAdapter() {

        val moonlightsQuery = getQuery(mDatabase!!)
        if (moonlightsQuery != null) {
            mFirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Moonlight, MoonlightViewHolder>(Moonlight::class.java, R.layout.moonlight_item, MoonlightViewHolder::class.java,
                    moonlightsQuery) {

                override fun populateViewHolder(viewHolder: MoonlightViewHolder, model: Moonlight, position: Int) {
                    val moonlightRef = getRef(position)
                    val moonlightKey = moonlightRef.key

                    val moonlightD = MoonlightEncryptUtils.newInstance().decryptMoonlight(model) ?: return

                    if (moonlightD.title != null) {
                        viewHolder.displayTitle(moonlightD.title)
                    } else {
                        viewHolder.mTitle.visibility = View.GONE
                    }

                    if (moonlightD.getContent() != null) {
                        viewHolder.displayContent(moonlightD.getContent())
                    } else {
                        viewHolder.mContent.visibility = View.GONE
                    }

                    if (moonlightD.getImageName() != null) {
                        Log.i(TAG, "populateViewHolder: " + moonlightD.getImageName())
                        viewHolder.mImage.setImageResource(R.drawable.ic_cloud_download_black_24dp)
                        //                        viewHolder.mImage.setTag(moonlightD.getImageName());
                        viewHolder.displayImage(mActivity, moonlightD.getImageUrl())
                    } else {
                        viewHolder.mImage.visibility = View.GONE
                    }

                    if (moonlightD.getColor() !== 0) {
                        viewHolder.setColor(moonlightD.getColor())
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            viewHolder.setColor(mActivity.getResources().getColor(R.color.white, null))
                        } else {
                            viewHolder.setColor(mActivity.getResources().getColor(R.color.white))
                        }
                    }

                    if (moonlightD.getAudioName() != null) {
                        val audioName = moonlightD.getAudioName()
                        if (!isAudioFileExists(audioName)) {
                            StorageUtils.downloadAudio(
                                    FirebaseStorage.getInstance().reference,
                                    uid, moonlightD.getAudioName())
                        }
                        viewHolder.mAudio.visibility = View.VISIBLE
                    } else {
                        viewHolder.mAudio.visibility = View.GONE
                    }



                    viewHolder.itemView.setOnClickListener {
                        if (isLogin) {
                            moonlightD.setId(moonlightKey)
                            if (isTrash) {
                                Log.d(TAG, "onClick: trash")
                                val trashDetailFragment = TrashDetailFragment.newInstance(moonlightD, 12)
                                FragmentUtils.replaceFragment(getFragmentManager(),
                                        R.id.main_fragment_container,
                                        trashDetailFragment,
                                        FragmentUtils.REPLACE_BACK_STACK)
                                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null)
                            } else {
                                Log.d(TAG, "onClick: edit")
                                val editMoonlightFragment = EditMoonlightFragment.newInstance(moonlightD, 0)
                                FragmentUtils.replaceFragment(getFragmentManager(),
                                        R.id.main_fragment_container,
                                        editMoonlightFragment,
                                        FragmentUtils.REPLACE_BACK_STACK)
                                BusEventUtils.post(Constants.BUS_FLAG_NONE_SECURITY, null)
                            }
                            changeToolbar(null, 1)
                            setParams(0)
                        }
                    }

                    viewHolder.itemView.setOnLongClickListener {
                        moonlightD.setId(moonlightKey)
                        if (moonlightKey == moonlightD.getId()) {
                            if (!isTrash) {
                                setParams(1)
                                changeToolbar(moonlightD, 0)
                            } else {
                                changeToolbar(moonlightD, 0)
                                setParams(1)
                            }
                        }
                        false
                    }
                }
            }

            mRecyclerView!!.adapter = mFirebaseRecyclerAdapter
            Log.d(TAG, "setAdapter")

            mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    mRecyclerView!!.smoothScrollToPosition(mFirebaseRecyclerAdapter!!.getItemCount())
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                    mRecyclerView!!.smoothScrollToPosition(toPosition)
                }
            }

            mFirebaseRecyclerAdapter!!.registerAdapterDataObserver(mAdapterDataObserver)

        }
    }

    private fun isAudioFileExists(audioName: String): Boolean {
        val dir = File(mActivity.getCacheDir(), "/audio")
        if (audioName.contains(".amr")) {
            val file = File(dir, audioName)
            return file.exists()
        } else {
            val file = File(dir, audioName + ".amr")
            return file.exists()
        }
    }


    private fun notifyChange() {
        Log.d(TAG, "notifyChange: ")
        Handler().postDelayed({ mFirebaseRecyclerAdapter!!.notifyDataSetChanged() }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFirebaseRecyclerAdapter != null) {
            Log.d(TAG, "cleanup")
            mFirebaseRecyclerAdapter!!.cleanup()
        }

        LogUtils.getInstance(TAG).setMessage("onDestroy").debug()

        val refWatcher = MoonlightApplication.getRefWatcher(mActivity)
        refWatcher.watch(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        LogUtils.getInstance(TAG).setMessage("onCreateOptionsMenu").debug()
        mMenu = menu
        mMenuInflater = inflater
        if (!isTrash) {
            changeOptionsMenu(3)
        } else {
            changeOptionsMenu(2)
        }
    }

    private fun changeOptionsMenu(type: Int) {
        if (mMenu != null) {
            mMenu!!.clear()
        }
        when (type) {
            0 -> mMenuInflater!!.inflate(R.menu.menu_long_click_moonlight, mMenu)
            1 -> mMenuInflater!!.inflate(R.menu.menu_long_click_trash, mMenu)
            2 -> mMenuInflater!!.inflate(R.menu.menu_trash, mMenu)
            3 -> {
                Log.d(TAG, "changeOptionsMenu: ")
                mMenuInflater!!.inflate(R.menu.menu_moonlight, mMenu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_data -> {
                val emptyNote = ConfirmationDialogFragment
                        .newInstance(uid, getString(R.string.dialog_empty_note_title),
                                getString(R.string.dialog_empty_note_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_NOTE)
                emptyNote.show(mActivity.getFragmentManager(), "Empty Note")
            }
            R.id.menu_empty_trash -> {
                val emptyTrash = ConfirmationDialogFragment
                        .newInstance(uid, getString(R.string.dialog_empty_trash_title),
                                getString(R.string.dialog_empty_trash_content),
                                Constants.EXTRA_TYPE_CDF_EMPTY_TRASH)
                emptyTrash.show(mActivity.getFragmentManager(), "Empty Trash")
                mActivity.setTitle(R.string.fragment_trash)
            }
            R.id.action_restore -> {
                FDatabaseUtils.restoreToNote(uid, mMoonlight)
                mActivity.setTitle(R.string.fragment_trash)
                changeOptionsMenu(3)
            }
            R.id.action_trash_delete_forever -> {
                StorageUtils.removePhoto(null, uid, mMoonlight!!.getImageName())
                StorageUtils.removeAudio(null, uid, mMoonlight!!.getAudioName())
                FDatabaseUtils.removeMoonlight(uid, mMoonlight!!.getId(), Constants.EXTRA_TYPE_DELETE_TRASH)
                mMoonlight = null
                mActivity.setTitle(R.string.fragment_trash)
                changeOptionsMenu(3)
            }
        }
        setParams(0)
        return super.onOptionsItemSelected(item)
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        super.onOptionsMenuClosed(menu)
    }

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    abstract fun getQuery(databaseReference: DatabaseReference): Query?

    abstract val isTrash: Boolean

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun busAction(busEvent: BusEvent?) {
        //这里更新视图或者后台操作,从busAction获取传递参数.
        if (busEvent != null) {
            when (busEvent!!.getFlag()) {
                Constants.BUS_FLAG_SIGN_OUT -> isLogin = false
            }
        }
    }

    private fun setParams(type: Int) {
        when (type) {
            0 -> mParams!!.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            1 -> mParams!!.scrollFlags = 0
        }
    }

    private fun changeToolbar(moonlight: Moonlight?, type: Int) {
        when (type) {
            0 -> {
                mToolbar!!.visibility = View.GONE
                mToolbar2!!.visibility = View.VISIBLE
                mToolbar2!!.title = null

                if (isTrash && !isInflate) {
                    mToolbar2!!.menu.clear()
                    mToolbar2!!.inflateMenu(R.menu.menu_long_click_trash)
                } else {
                    if (!isInflate) {
                        mToolbar2!!.menu.clear()
                        mToolbar2!!.inflateMenu(R.menu.menu_long_click_moonlight)
                    }
                }


                mToolbar2!!.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
                mToolbar2!!.setNavigationOnClickListener {
                    setParams(0)
                    changeToolbar(null, 1)
                    isInflate = true
                }
                mToolbar2!!.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete -> {
                            FDatabaseUtils.moveToTrash(uid, moonlight)
                            mActivity.setTitle(R.string.app_name)
                        }
                        R.id.action_delete_forever -> {
                            if (moonlight!!.getImageUrl() != null) {
                                StorageUtils.removePhoto(null, uid, moonlight!!.getImageName())
                            }
                            if (moonlight!!.getAudioUrl() != null) {
                                StorageUtils.removeAudio(null, uid, moonlight!!.getAudioName())
                            }
                            FDatabaseUtils.removeMoonlight(uid,
                                    moonlight!!.getId(),
                                    Constants.EXTRA_TYPE_MOONLIGHT)
                            mActivity.setTitle(R.string.app_name)
                        }
                        R.id.action_make_a_copy -> {
                            FDatabaseUtils.addMoonlight(uid,
                                    moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
                            mActivity.setTitle(R.string.app_name)
                        }
                        R.id.action_send -> {
                            //启动Intent分享
                            var `in` = Intent(Intent.ACTION_SEND)
                            `in`.type = "text/plain"
                            if (moonlight!!.title != null) {
                                `in`.putExtra(Intent.EXTRA_TITLE, moonlight!!.title())
                            }

                            if (moonlight!!.getContent() != null) {
                                `in`.putExtra(Intent.EXTRA_TEXT, moonlight!!.getContent())
                            }

                            if (moonlight!!.getImageUrl() != null) {
                                `in`.putExtra(Intent.EXTRA_TEXT, moonlight!!.getImageUrl())
                            }
                            //设置分享选择器
                            `in` = Intent.createChooser(`in`, "Send to")
                            startActivity(`in`)
                            mActivity.setTitle(R.string.app_name)
                        }
                        R.id.action_restore -> {
                            FDatabaseUtils.restoreToNote(uid, moonlight)
                            mActivity.setTitle(R.string.fragment_trash)
                        }
                        R.id.action_trash_delete_forever -> {
                            if (moonlight!!.getImageUrl() != null) {
                                StorageUtils.removePhoto(null, uid, moonlight!!.getImageName())
                            }
                            if (moonlight!!.getAudioUrl() != null) {
                                StorageUtils.removeAudio(null, uid, moonlight!!.getAudioName())
                            }
                            FDatabaseUtils.removeMoonlight(uid,
                                    moonlight!!.getId(),
                                    Constants.EXTRA_TYPE_DELETE_TRASH)
                            mActivity.setTitle(R.string.fragment_trash)
                        }
                    }
                    setParams(0)
                    changeToolbar(null, 1)
                    changeOptionsMenu(3)
                    isInflate = true
                    false
                }
            }
            1 -> {
                mToolbar2!!.visibility = View.GONE
                mToolbar!!.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private val TAG = "MoonlightListFragment"
    }
}
