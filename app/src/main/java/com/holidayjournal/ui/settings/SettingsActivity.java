package com.holidayjournal.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.holidayjournal.R;
import com.holidayjournal.ui.auth.AuthActivity;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.utils.Utils;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity implements SettingsView, SettingsFragment.SettingsFragmentListener {

    @BindView(R.id.settings_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.settings_progress)
    ProgressBar mProgressBar;

    SettingsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new SettingsPresenter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_settings;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public void onDeleteData() {

    }

    @Override
    public void onDeleteAccount() {
        showProgressBar();
        mPresenter.deleteUserAccount();
    }

    @Override
    public void onDataDeleted() {
        showToast("Data successfully deleted!");
        hideProgressBar();
    }

    @Override
    public void onAccountDeleted() {
        if (Utils.isGoogleAccount(this)) {
            revokeAccessToGoogleAccount();
        } else {
            redirectToLogin();
        }
    }

    @Override
    public void onError(String message) {
        showToast(message);
        hideProgressBar();
    }

    private void revokeAccessToGoogleAccount() {
        Utils.getGoogleSignInClient(this).revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        redirectToLogin();
                    }
                });
    }

    private void redirectToLogin() {
        showToast("Account successfully deleted.");
        hideProgressBar();

        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showProgressBar() {
        if (!mProgressBar.isShown()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (mProgressBar.isShown()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
