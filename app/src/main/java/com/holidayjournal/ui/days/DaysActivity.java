package com.holidayjournal.ui.days;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.holidayjournal.R;
import com.holidayjournal.models.DayModel;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.utils.Intents;

import butterknife.BindView;

public class DaysActivity extends BaseActivity implements DaysView {

    @BindView(R.id.days_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.days_recycler_view)
    RecyclerView mRecyclerView;

    private final int EDIT_DAY_REQ = 1;

    private DaysAdapter mAdapter;
    private HolidayModel holiday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.holiday = getIntent().getParcelableExtra(Constants.HOLIDAY);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(this.holiday.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initAdapter();
        mAdapter.loadDays(this.holiday.getDays());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_days;
    }

    private void initAdapter() {
        mAdapter = new DaysAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateHoliday(DayModel day) {
        this.holiday.getDays().set((day.getNr() - 1), day);
        this.holiday.calcRating();

        DaysPresenter mPresenter = new DaysPresenter(this);
        mPresenter.update(this.holiday);

        mAdapter.updateDay(day);
    }

    @Override
    public void onUpdateHolidaySuccess() {
        Intent intent = new Intent(Intents.HOLIDAY_UPDATED);
        intent.putExtra(Constants.HOLIDAY, this.holiday);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        showToast("Successfully updated your day!");
    }

    @Override
    public void onError(String message) {
        showToast(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_DAY_REQ) {
                if (data != null && data.getExtras() != null) {
                    DayModel day = data.getParcelableExtra(Constants.DAY);
                    updateHoliday(day);
                }
            }
        }
    }
}
