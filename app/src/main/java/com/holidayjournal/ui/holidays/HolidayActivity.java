package com.holidayjournal.ui.holidays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holidayjournal.R;
import com.holidayjournal.services.NotificationService;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.holidays.addholiday.AddHolidayActivity;
import com.holidayjournal.utils.Intents;

import org.joda.time.DateTime;

import butterknife.BindView;

public class HolidayActivity extends BaseActivity implements HolidayView, View.OnClickListener {

    @BindView(R.id.holiday_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.holidays_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.holiday_empty_screen)
    TextView mEmptyScreen;

    @BindView(R.id.holiday_fab)
    FloatingActionButton mFab;

    @BindView(R.id.holiday_progress)
    ProgressBar mProgressBar;

    private final int ADD_HOLIDAY_REQ = 0;
    private HolidayAdapter mAdapter;
    private HolidayPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        showProgressBar();

        initAdapter();

        registerBroadcastReceiver();

        startNotificationService();

        mFab.setOnClickListener(this);

        mPresenter = new HolidayPresenter(this);
        mPresenter.getHolidays();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_holiday;
    }

    private void initAdapter() {
        mAdapter = new HolidayAdapter(this);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.holiday_fab:
                Intent addHolidayIntent = new Intent(this, AddHolidayActivity.class);
                startActivityForResult(addHolidayIntent, ADD_HOLIDAY_REQ);
                break;
        }
    }

    @Override
    public void onDownloadHolidaySuccess(HolidayModel holiday) {
        mAdapter.addHoliday(holiday);
        mRecyclerView.smoothScrollToPosition(0);
        hideProgressBar();
    }

    @Override
    public void noHolidays() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyScreen.setVisibility(View.VISIBLE);
        hideProgressBar();
    }

    public void deleteHoliday(HolidayModel holiday, int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.delete_holiday));
        dialog.setMessage(getString(R.string.delete_holiday_confirmation, holiday.getTitle()));

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteHoliday(holiday);
                mAdapter.deleteHoliday(position);
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
    public void onHolidayDeleted() {
        if (mAdapter.getItemCount() == 0) {
            noHolidays();
        }
    }

    @Override
    public void onError(String message) {
        showToast(message);
        hideProgressBar();
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

    private void registerBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(Intents.HOLIDAY_UPDATED));
    }

    private void unregisterBroadcastReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    private void startNotificationService() {
        DateTime date = new DateTime();
        date = date.plusDays(1);
        long millis = date.getMillis();

        Intent notificationService = new Intent(this, NotificationService.class);
        notificationService.putExtra("date", millis);
        this.startService(notificationService);
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_HOLIDAY_REQ) {

                HolidayModel holiday = data.getParcelableExtra(Constants.HOLIDAY);
                boolean isEdit = data.getBooleanExtra(Constants.EDIT_HOLIDAY, false);

                if (isEdit) {
                    mAdapter.updateHoliday(holiday);
                } else {
                    mAdapter.addHoliday(holiday);
                    mRecyclerView.smoothScrollToPosition(0);
                }

                if (mRecyclerView.getVisibility() == View.GONE) {
                    mEmptyScreen.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HolidayModel holiday = intent.getParcelableExtra(Constants.HOLIDAY);
            if (holiday != null) {
                mAdapter.updateHoliday(holiday);
            }
        }
    };

}
