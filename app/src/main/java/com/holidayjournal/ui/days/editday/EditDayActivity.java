package com.holidayjournal.ui.days.editday;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.holidayjournal.R;
import com.holidayjournal.models.DayModel;
import com.holidayjournal.models.LocationModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.ui.maps.LocationActivity;
import com.holidayjournal.utils.Constants;

import butterknife.BindView;

public class EditDayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edit_day_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.edit_day_title)
    EditText mTitle;

    @BindView(R.id.edit_day_location)
    EditText mLocation;

    @BindView(R.id.edit_day_rating_spinner)
    Spinner mRatingSpinner;

    @BindView(R.id.edit_day_desc)
    EditText mDesc;

    @BindView(R.id.edit_day_save_btn)
    Button mSaveBtn;

    private final int PICK_LOCATION_REQ = 0;
    private DayModel day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        day = getIntent().getParcelableExtra(Constants.DAY);

        setLayout();

        mLocation.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_edit_day;
    }

    private void setLayout() {
        initRatingSpinner();
        if (day != null) {
            if (!TextUtils.isEmpty(day.getTitle())) {
                mTitle.setText(day.getTitle());
            }
            if (!TextUtils.isEmpty(day.getDescription())) {
                mDesc.setText(day.getDescription());
            }
            if (day.getLocation() != null) {
                mLocation.setText(day.getLocation().getName());
            }
        }
    }

    private void initRatingSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRatingSpinner.setAdapter(adapter);

        if (day.getRating() != 0) {
            int spinnerPosition = adapter.getPosition(String.valueOf(day.getRating()));
            mRatingSpinner.setSelection(spinnerPosition);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_day_location:
                Intent intent = new Intent(this, LocationActivity.class);
                if (day.getLocation() != null) {
                    intent.putExtra(Constants.LOCATION, day.getLocation());
                }
                startActivityForResult(intent, PICK_LOCATION_REQ);
                break;

            case R.id.edit_day_save_btn:
                saveDay();
                break;
        }
    }

    private boolean isValidDay(DayModel day) {
        if (TextUtils.isEmpty(day.getTitle())) {
            showToast("Title may not be empty!");
            return false;
        }
        return true;
    }

    private void saveDay() {
        day.setTitle(mTitle.getText().toString());
        day.setDescription(mDesc.getText().toString());
        try {
            day.setRating(Integer.parseInt(mRatingSpinner.getSelectedItem().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isValidDay(day)) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Constants.DAY, day);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_LOCATION_REQ) {
                if (data != null && data.getExtras() != null) {
                    LocationModel locationModel = data.getParcelableExtra(Constants.LOCATION);
                    mLocation.setText(locationModel.getName());
                    day.setLocation(locationModel);
                }
            }
        }
    }
}
