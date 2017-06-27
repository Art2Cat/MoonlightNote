package com.art2cat.dev.moonlightnote.controller.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.ContentFrameLayout
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.BaseFragment
import com.art2cat.dev.moonlightnote.controller.common_dialog_fragment.InputDialogFragment
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity
import com.art2cat.dev.moonlightnote.model.BusEvent
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.model.Moonlight
import com.art2cat.dev.moonlightnote.model.User
import com.art2cat.dev.moonlightnote.utils.SPUtils
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils
import com.art2cat.dev.moonlightnote.utils.UserUtils
import com.art2cat.dev.moonlightnote.utils.firebase.AuthUtils
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Rorschach
 * on 21/05/2017 12:20 AM.
 *
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseFragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    protected var mView: View? = null
    private var mEmailView: AppCompatEditText? = null
    private var mPasswordView: AppCompatEditText? = null
    private var mProgressView: View? = null
    private var mLoginFormView: View? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var flag = 0
    private var isNewUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        EventBus.getDefault().register(this)
        signIn()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mView = inflater!!.inflate(R.layout.fragment_login, container, false)

        mEmailView = mView?.findViewById(R.id.email)

        val toolbar: Toolbar? = mView?.findViewById(R.id.toolbar)
        (activity as LoginActivity).setSupportActionBar(toolbar)
        toolbar!!.setTitle(R.string.fragment_login)

        mPasswordView = mView?.findViewById(R.id.password)
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        val reset: AppCompatButton = mView?.findViewById(R.id.reset_password)!!
        val mRegister: AppCompatButton = mView?.findViewById(R.id.email_sign_in_button)!!
        val mLogin: AppCompatButton = mView?.findViewById(R.id.email_sign_up_button)!!
        val mLogin_Google: AppCompatButton = mView?.findViewById(R.id.login_google_btn)!!
        val test: AppCompatButton = mView?.findViewById(R.id.test_btn)!!

        reset.setOnClickListener(this)
        mRegister.setOnClickListener(this)
        mLogin.setOnClickListener(this)
        mLogin_Google.setOnClickListener(this)
        test.setOnClickListener(this)

        mLoginFormView = mView?.findViewById(R.id.login_form)
        mProgressView = mView?.findViewById(R.id.login_progress)
        return mView as View
    }

    override fun onStart() {
        super.onStart()
        addListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        removeListener()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.stopAutoManage(activity as FragmentActivity)
            mGoogleApiClient!!.disconnect()
        }
        super.onDestroy()
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        mEmailView!!.error = null
        mPasswordView!!.error = null

        // Store values at the date of the login attempt.
        val email = mEmailView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView!!.error = getString(R.string.error_field_required)
            focusView = mPasswordView
            cancel = true
        } else if (!isPasswordValid(password)) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView!!.error = getString(R.string.error_field_required)
            focusView = mEmailView
            cancel = true
        } else if (!isEmailValid(email)) {
            mEmailView!!.error = getString(R.string.error_invalid_email)
            focusView = mEmailView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            showProgress(true)
            if (flag == 1) {
                signUp(email, password)

                flag = 2
            } else if (flag == 2) {
                signInWithEmail(email, password)
                //Intent intent = new Intent(getActivity(), MoonlightDetailActivity.class);
                //startActivity(intent);
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
        mLoginFormView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 0 else 1).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLoginFormView!!.visibility = if (show) View.GONE else View.VISIBLE
            }
        })

        mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
        mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            Log.d(TAG, "onActivityResult: " + result.status)
            if (result.isSuccess) {
                val account = result.signInAccount
                if (account != null) firebaseAuthWithGoogle(account)

                isNewUser = true
            } else {
                showProgress(false)
                showShortSnackBar((mView as ContentFrameLayout?)!!, "Google Sign In failed", SnackBarUtils.TYPE_INFO)
                Log.d(TAG, "Google Sign In failed")
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.reset_password -> {
                mEmailView!!.error = null
                val email = mEmailView!!.text.toString()
                if (TextUtils.isEmpty(email)) {
                    val inputDialogFragment = InputDialogFragment
                            .newInstance(getString(R.string.dialog_reset_password), 0)
                    inputDialogFragment.show(mActivity.fragmentManager, "resetPassword")
                } else {
                    AuthUtils.sendRPEmail(MoonlightApplication.context!!, mView!!, email)
                }
            }
            R.id.email_sign_in_button -> {
                flag = 2
                attemptLogin()
            }
            R.id.email_sign_up_button -> {
                flag = 1
                attemptLogin()
            }
            R.id.login_google_btn -> {
                showProgress(true)
                val mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                mGoogleApiClient = null
                mGoogleApiClient = GoogleApiClient.Builder(activity)
                        .enableAutoManage(activity as FragmentActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                        .build()
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
            R.id.test_btn ->
                //                Utils.openMailClient(getActivity());
                signInAnonymously()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        showShortSnackBar((mView as ContentFrameLayout?)!!, connectionResult.errorMessage!!, SnackBarUtils.TYPE_ALERT)
    }

    fun signIn() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
                SPUtils.putString(MoonlightApplication.context!!, "User", "Id", user.uid)
                val photoUrl = user.photoUrl
                val nickname = user.displayName
                val email = user.email
                val token = FirebaseInstanceId.getInstance().token
                val user1 = User()
                user1.uid = user.uid
                if (nickname != null) {
                    user1.nickname = nickname
                }
                if (email != null) {
                    user1.email = email
                }
                if (photoUrl != null) {
                    user1.photoUrl = photoUrl.toString()
                }

                if (token != null) {
                    user1.token = token
                }

                user1.encryptKey = user.uid

                if (isNewUser) {
                    commitMoonlight(user.uid)
                }

                UserUtils.updateUser(user.uid, user1)
                UserUtils.saveUserToCache(MoonlightApplication.context, user1)
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out:")
            }
        }
    }

    @SuppressLint("LogConditional")
    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)

                    if (!task.isSuccessful) {
                        showProgress(false)
                        Log.w(TAG, "signInWithCredential", task.exception)
                        //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
                        val exception = task.exception!!.message
                        showShortSnackBar((mView as ContentFrameLayout?)!!, "Authentication failed: " + exception,
                                SnackBarUtils.TYPE_WARNING)
                    } else {
                        showProgress(false)
                        val intent = Intent(activity, MoonlightActivity::class.java)
                        val bundle = ActivityOptions
                                .makeSceneTransitionAnimation(activity).toBundle()
                        activity.startActivity(intent, bundle)
                        showShortSnackBar(mView!!, "Google Sign In succeed", SnackBarUtils.TYPE_INFO)
                        isNewUser = true
                        SPUtils.putBoolean(MoonlightApplication.context!!, "User", "google", true)
                        activity.finishAfterTransition()
                    }
                }
    }

    fun signInWithEmail(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showProgress(false)
                        val intent = Intent(activity, MoonlightActivity::class.java)
                        val bundle = ActivityOptions
                                .makeSceneTransitionAnimation(activity).toBundle()
                        activity.startActivity(intent, bundle)

                        //销毁当前Activity
                        activity.finishAfterTransition()
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)
                    }
                }.addOnFailureListener { e ->
            showProgress(false)
            showShortSnackBar(mView!!, "Sign In Failed: " + e.toString(),
                    SnackBarUtils.TYPE_WARNING)
        }
    }

    fun signUp(email: String, password: String) {

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showProgress(false)
                        showLongSnackBar(mView!!, "Sign Up succeed!",
                                SnackBarUtils.TYPE_WARNING)
                        val user = FirebaseAuth.getInstance().currentUser!!
                        user.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "Email sent.")
                                    }
                                }

                        signInWithEmail(email, password)
                        isNewUser = true
                    } else {
                        showProgress(false)
                        isNewUser = false
                        //noinspection ThrowableResultOfMethodCallIgnored,ConstantConditions
                        val exception = task.exception!!.message
                        showLongSnackBar(mView!!, "Sign Up Failed: " + exception,
                                SnackBarUtils.TYPE_WARNING)
                    }
                }.addOnFailureListener { e -> if (BuildConfig.DEBUG) Log.d(TAG, e.toString()) }
    }

    private fun signInAnonymously() {
        showProgress(true)
        mAuth!!.signInAnonymously()
                .addOnCompleteListener { task ->
                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        val intent = Intent(activity, MoonlightActivity::class.java)
                        activity.startActivity(intent)
                    }

                    showProgress(false)
                }
    }


    fun addListener() {
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    fun removeListener() {
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun busAction(busEvent: BusEvent) {
        if (busEvent.message.contains("@")) {
            AuthUtils.sendRPEmail(MoonlightApplication.context!!, mView!!, busEvent.message)
        } else {
            showShortSnackBar(mView!!, "Invalid email address",
                    SnackBarUtils.TYPE_WARNING)
        }
    }

    private fun commitMoonlight(userId: String) {
        val moonlight = Moonlight()
        moonlight.title = getString(R.string.init_moonlight_title)
        moonlight.content = getString(R.string.init_moonlight_content)
        FDatabaseUtils.addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
    }

    companion object {
        private val RC_SIGN_IN = 9001
        private val TAG = "LoginFragment"
    }
}// Required empty public constructor
