package com.art2cat.dev.moonlightnote.controller.user;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChangePasswordFragment extends BaseFragment {

    private static final String TAG = "ChangePasswordFragment";

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        TextInputEditText oldET = view.findViewById(R.id.old_password_editText);
        TextInputEditText newET = view.findViewById(R.id.new_password_editText);
        AppCompatButton button = view.findViewById(R.id.change_password);


        setHasOptionsMenu(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        button.setOnClickListener(v -> {
            String oldPassword = oldET.getText().toString();
            final String newPassword = newET.getText().toString();

            if (!oldPassword.equals("") && !newPassword.equals("")) {
                AuthCredential credential = null;
                if (user.getEmail() != null) {
                    credential = EmailAuthProvider
                            .getCredential(user.getEmail(), oldPassword);
                }
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");
                                                Snackbar snackbar = SnackBarUtils
                                                        .shortSnackBar(v, "Password updated"
                                                                , SnackBarUtils.TYPE_INFO);
                                                snackbar.show();
                                                // 当snackbar显示消失是，启动回退栈
                                                snackbar.setCallback(new Snackbar.Callback() {
                                                    @Override
                                                    public void onDismissed(Snackbar snackbar, int event) {
                                                        getActivity().onBackPressed();
                                                        super.onDismissed(snackbar, event);
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(e -> SnackBarUtils.longSnackBar(v, e.toString(),
                                    SnackBarUtils.TYPE_INFO).show());
                        }
                    }
                }).addOnFailureListener(e -> SnackBarUtils.longSnackBar(v, e.toString(),
                        SnackBarUtils.TYPE_INFO).show());
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
