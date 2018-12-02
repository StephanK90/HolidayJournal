package com.holidayjournal.ui.holidays.addholiday;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.holidayjournal.R;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.ui.base.BaseActivity;
import com.holidayjournal.utils.DateFormatter;
import com.holidayjournal.utils.ImageCompressor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;

public class AddHolidayActivity extends BaseActivity implements AddHolidayView, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.add_holiday_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.add_holiday_progress)
    ProgressBar mProgressBar;

    @BindView(R.id.add_holiday_title)
    EditText mTitle;

    @BindView(R.id.add_holiday_start_date)
    EditText mStartDate;

    @BindView(R.id.add_holiday_end_date)
    EditText mEndDate;

    @BindView(R.id.add_holiday_desc)
    EditText mDesc;

    @BindView(R.id.add_holiday_image)
    ImageView mImage;

    @BindView(R.id.add_holiday_btn)
    Button mAddBtn;

    private final int IMAGE_REQ_ID = 0;
    private final int STORAGE_REQ_ID = 1;

    private AddHolidayPresenter mPresenter;
    private boolean isEdit;
    private HolidayModel holiday;
    private Calendar myCalendar;
    private boolean isStartDate;
    //private boolean storagePermission;
    private Uri compressedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPresenter = new AddHolidayPresenter(this);

        if (getIntent().getExtras() != null) {
            holiday = getIntent().getParcelableExtra(Constants.HOLIDAY);
            isEdit = true;
            setEditHolidayLayout();
        }

        mAddBtn.setOnClickListener(this);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mImage.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_add_holiday;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_holiday_btn:
                addHoliday();
                break;
            case R.id.add_holiday_start_date:
                isStartDate = true;
                showDatePickerDialog();
                break;
            case R.id.add_holiday_end_date:
                isStartDate = false;
                showDatePickerDialog();
                break;
            case R.id.add_holiday_image:
                verifyStoragePermissions();
                break;
        }
    }

    private void setEditHolidayLayout() {
        setActionBarTitle(getString(R.string.edit_holiday_toolbar_title));
        mAddBtn.setText(getString(R.string.save));
        mStartDate.setEnabled(false);
        mEndDate.setEnabled(false);

        mTitle.setText(holiday.getTitle());
        mDesc.setText(holiday.getDescription());
        mStartDate.setText(DateFormatter.toString(holiday.getStartDate()));
        mEndDate.setText(DateFormatter.toString(holiday.getEndDate()));
        if (!TextUtils.isEmpty(holiday.getImageUri())) {
            Glide.with(this).load(holiday.getImageUri()).into(mImage);
        }
    }

    private void showDatePickerDialog() {
        myCalendar = Calendar.getInstance();
        new DatePickerDialog(this, this,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        setHolidayDate();
    }

    private void setHolidayDate() {
        String dateFormat = "d MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        String date = sdf.format(myCalendar.getTime());

        if (isStartDate) {
            mStartDate.setText(date);
        } else {
            mEndDate.setText(date);
        }
    }

    private void addHoliday() {
        showProgressBar();
        long startDate = DateFormatter.toLong(mStartDate.getText().toString());
        long endDate = DateFormatter.toLong(mEndDate.getText().toString());

        if (isEdit) {
            editHoliday(startDate, endDate);

        } else {
            holiday = new HolidayModel(mTitle.getText().toString(), startDate, endDate);
            holiday.setDescription(mDesc.getText().toString());
            if (compressedImageUri != null) {
                holiday.setImageName(compressedImageUri.getLastPathSegment());
            }

            mPresenter.addHoliday(holiday, compressedImageUri);
        }
    }

    private void editHoliday(long startDate, long endDate) {
        holiday.setTitle(mTitle.getText().toString());
        holiday.setStartDate(startDate);
        holiday.setEndDate(endDate);
        holiday.setDescription(mDesc.getText().toString());
        if (compressedImageUri != null) {
            holiday.setImageName(compressedImageUri.getLastPathSegment());
        }

        mPresenter.editHoliday(holiday, compressedImageUri);
    }

    @Override
    public void onAddHolidaySuccess(String holidayId) {
        holiday.setId(holidayId);
        if (compressedImageUri != null) {
            holiday.setImageUri(compressedImageUri.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(Constants.HOLIDAY, holiday);
        intent.putExtra(Constants.EDIT_HOLIDAY, isEdit);

        hideProgressBar();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intent, getString(R.string.select_picture));

        /*
        String imageFileName = "IMAGE_" + System.currentTimeMillis();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();


        if (storagePermission) {
            File image = null;
            try {
                image = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (image != null) {
                savedImageUri = Uri.fromFile(image);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedImageUri);
                    ArrayList<Intent> intents = new ArrayList<>();
                    intents.add(cameraIntent);
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[1]));
                }
            }
        }
        */

        startActivityForResult(chooser, IMAGE_REQ_ID);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQ_ID) {
                if (data != null && data.getData() != null) {
                    File actualImage = null;
                    try {
                        actualImage = ImageCompressor.from(this, data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("Unable to retrieve image.");
                    }
                    try {
                        if (actualImage != null) {
                            compressedImageUri = Uri.fromFile(ImageCompressor.compressImage(this, actualImage));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("Failed to compress image.");
                    }

                    if (compressedImageUri != null) {
                        Glide.with(this).load(compressedImageUri).into(mImage);
                    }
                }
            }
        }
    }

    private void verifyStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQ_ID);

        } else {
            //storagePermission = true;
            openImageChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQ_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //storagePermission = true;
                    openImageChooser();
                }
                break;
            }
        }
    }

}