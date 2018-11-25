package com.holidayjournal.ui.holidays.addholiday;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.utils.Utils;

class AddHolidayPresenter {

    private AddHolidayView mView;

    AddHolidayPresenter(AddHolidayView view) {
        this.mView = view;
    }

    void addHoliday(HolidayModel holiday, Uri image) {
        if (Utils.getUserId() == null || notValid(holiday)) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USERS)
                .document(Utils.getUserId())
                .collection(Constants.HOLIDAYS)
                .add(holiday)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (image != null) {
                            uploadImage(image, documentReference.getId());
                        } else {
                            mView.onAddHolidaySuccess(documentReference.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        mView.onError("Failed to add Holiday.");
                    }
                });
    }

    void editHoliday(HolidayModel holiday, Uri image) {
        if (Utils.getUserId() == null || notValid(holiday)) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.USERS)
                .document(Utils.getUserId())
                .collection(Constants.HOLIDAYS)
                .document(holiday.getId())
                .set(holiday)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (image != null) {
                            uploadImage(image, holiday.getId());
                        } else {
                            mView.onAddHolidaySuccess(holiday.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        mView.onError("Failed to edit Holiday");
                    }
                });
    }

    private void uploadImage(Uri image, String holidayId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReference()
                .child(Constants.USERS)
                .child(Utils.getUserId())
                .child(holidayId)
                .putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mView.onAddHolidaySuccess(holidayId);
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        mView.onError("Failed to add Holiday.");
                    }
                });
    }

    private boolean notValid(HolidayModel holiday) {
        if (TextUtils.isEmpty(holiday.getTitle())) {
            mView.onError("Title required!");
            return true;
        }

        if (holiday.getStartDate() <= 0) {
            mView.onError("Start date required!");
            return true;
        }

        if (holiday.getEndDate() <= 0) {
            mView.onError("End date required!");
            return true;
        }

        if (holiday.getStartDate() > holiday.getEndDate()) {
            mView.onError("End date can not be before start date!");
            return true;
        }

        return false;
    }

}
