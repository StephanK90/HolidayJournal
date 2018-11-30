package com.holidayjournal.ui.holidays;

import com.holidayjournal.models.HolidayModel;


interface HolidayView {

    void onDownloadHolidaySuccess(HolidayModel holiday);

    void onHolidayDeleted();

    void noHolidays();

    void onNextHolidayDate(long nextHolidayDate);

    void onError(String message);

}
