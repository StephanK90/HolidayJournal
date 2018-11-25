package com.holidayjournal.ui.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;

import com.holidayjournal.R;

public class SettingsFragment extends PreferenceFragment implements SettingsView {

    private SettingsFragmentListener mCallback;
    private SettingsFragmentPresenter mPresenter;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        mPresenter = new SettingsFragmentPresenter(this);

        Preference delete_data_btn = findPreference(getActivity().getString(R.string.pref_delete_data));
        delete_data_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });

        Preference delete_account_btn = findPreference(getActivity().getString(R.string.pref_delete_account));
        delete_account_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDeleteAccountDialog();
                return true;
            }
        });
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.delete_account));
        dialog.setMessage(getString(R.string.delete_account_confirmation));

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteUserAccount();
            }
        });

        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onDataDeleted() {

    }

    @Override
    public void onAccountDeleted() {
        mCallback.redirectToLogin();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (SettingsFragmentListener) activity;
    }

    public interface SettingsFragmentListener {
        void redirectToLogin();
    }
}
