package com.art2cat.dev.moonlightnote.controller.user


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_change_password, container, false)
        val oldET: TextInputEditText = view.findViewById(R.id.old_password_editText)
        val newET: TextInputEditText = view.findViewById(R.id.new_password_editText)
        val button: AppCompatButton = view.findViewById(R.id.change_password)


        setHasOptionsMenu(true)
        val user = FirebaseAuth.getInstance().currentUser

        button.setOnClickListener { v ->
            val oldPassword = oldET.text.toString()
            val newPassword = newET.text.toString()

            if (oldPassword != "" && newPassword != "") {
                val credential = EmailAuthProvider
                        .getCredential(user!!.email!!, oldPassword)
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User password updated.")
                                        val snackbar = SnackBarUtils
                                                .shortSnackBar(v, "Password updated", SnackBarUtils.TYPE_INFO)
                                        snackbar.show()
                                        // 当snackbar显示消失是，启动回退栈
                                        snackbar.setCallback(object : Snackbar.Callback() {
                                            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                                                activity.onBackPressed()
                                                super.onDismissed(snackbar, event)
                                            }
                                        })
                                    }
                                }.addOnFailureListener { e ->
                            SnackBarUtils.longSnackBar(v, e.toString(),
                                    SnackBarUtils.TYPE_INFO).show()
                        }
                    }
                }.addOnFailureListener { e ->
                    SnackBarUtils.longSnackBar(v, e.toString(),
                            SnackBarUtils.TYPE_INFO).show()
                }
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> activity.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = "ChangePasswordFragment"
    }
}// Required empty public constructor
