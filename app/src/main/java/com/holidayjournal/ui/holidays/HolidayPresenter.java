package com.holidayjournal.ui.holidays;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.utils.DateFormatter;
import com.holidayjournal.utils.Utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

class HolidayPresenter {

    private HolidayView mView;
    private DateTime nextHolidayDate;

    HolidayPresenter(HolidayView view) {
        this.mView = view;
    }

    void getHolidays() {
        if (Utils.getUserId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.USERS)
                    .document(Utils.getUserId())
                    .collection(Constants.HOLIDAYS)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (!task.isSuccessful() || task.getResult() == null) {
                                mView.onError("Unable to download holidays.");
                                return;
                            }

                            if (task.getResult().isEmpty()) {
                                mView.noHolidays();
                                return;
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HolidayModel holiday = document.toObject(HolidayModel.class);
                                holiday.setId(document.getId());

                                String imageName = holiday.getImageName();
                                if (!TextUtils.isEmpty(imageName)) {
                                    getHolidayImage(holiday);

                                } else {
                                    mView.onDownloadHolidaySuccess(holiday);
                                }

                                checkNextHolidayDate(holiday.getStartDate());
                            }

                            returnNextHolidayDate();
                        }
                    });
        }
    }

    private void getHolidayImage(HolidayModel holiday) {
        if (Utils.getUserId() != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference()
                    .child(Constants.USERS)
                    .child(Utils.getUserId())
                    .child(holiday.getId())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri image) {
                            holiday.setImageUri(image.toString());
                            mView.onDownloadHolidaySuccess(holiday);
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            mView.onError("Unable to download holidays.");
                        }
                    });
        }
    }

    void deleteHoliday(HolidayModel holiday) {
        if (Utils.getUserId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.USERS)
                    .document(Utils.getUserId())
                    .collection(Constants.HOLIDAYS)
                    .document(holiday.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (!TextUtils.isEmpty(holiday.getImageUri())) {
                                deleteHolidayImage(holiday.getId());
                            } else {
                                mView.onHolidayDeleted();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mView.onError("Failed to delete holiday.");
                        }
                    });
        }
    }

    private void deleteHolidayImage(String holidayId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference()
                .child(Constants.USERS)
                .child(Utils.getUserId())
                .child(holidayId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mView.onHolidayDeleted();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mView.onError("Failed to delete holiday image.");
                    }
                });
    }

    private void checkNextHolidayDate(long date) {
        DateTime startDate = DateFormatter.toDate(date);

        if (nextHolidayDate == null && startDate.toLocalDate().isAfter(LocalDate.now())) {
            nextHolidayDate = startDate;

        } else if (nextHolidayDate != null && startDate.toLocalDate().isAfter(LocalDate.now())
                && startDate.toLocalDate().isBefore(nextHolidayDate.toLocalDate())) {
            nextHolidayDate = startDate;
        }
    }

    private void returnNextHolidayDate() {
        if (nextHolidayDate != null) {
            mView.onNextHolidayDate(nextHolidayDate.getMillis());
        }
    }

}