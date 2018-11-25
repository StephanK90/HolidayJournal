package com.holidayjournal.ui.days;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.holidayjournal.models.HolidayModel;
import com.holidayjournal.utils.Constants;
import com.holidayjournal.utils.Utils;

class DaysPresenter {

    private DaysView mView;

    DaysPresenter(DaysView view) {
        this.mView = view;
    }

    void update(HolidayModel holiday) {
        if (Utils.getUserId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.USERS)
                    .document(Utils.getUserId())
                    .collection(Constants.HOLIDAYS)
                    .document(holiday.getId())
                    .set(holiday)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mView.onUpdateHolidaySuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            mView.onError("Failed updating your day.");
                        }
                    });
        }
    }
}
